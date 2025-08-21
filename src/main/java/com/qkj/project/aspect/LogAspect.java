package com.qkj.project.aspect;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qkj.project.common.RequestHolder;
import com.qkj.project.common.Result;
import com.qkj.project.common.annotations.ULog;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.dao.OptionLogDao;
import com.qkj.project.entity.OptionLog;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 日志切面处理类
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Resource(name = "logExecutorService")
    private ExecutorService logExecutorService;

    @Resource
    private OptionLogDao optionLogDao;

    @Pointcut("execution(public * com.qkj.project.controller.*.*(..))")
    public void checkUserIdPointcut() {}

    /**
     * 日志记录前置通知，用于拦截Controller层记录用户的操作
     * @param joinPoint 切入点对象
     */
    @Order(1)
    @Before("checkUserIdPointcut()")
    public void check(JoinPoint joinPoint) throws JsonProcessingException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        List<Object> ars = new ArrayList<>();
        String ip = request.getHeader("x-forwarded-for");
        if (BaseUtil.isEmpty(ip)) {
            ip =  request.getRemoteAddr();
        }
        if (!"GET".equals(request.getMethod())) {
            for (Object o : joinPoint.getArgs()) {
                if (Objects.isNull(o) || o instanceof MultipartFile || o instanceof MultipartFile[]
                        || o instanceof HttpServletRequest || o instanceof HttpServletResponse) {
                    continue;
                }
                if (o instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) o;
                    // 进一步检查Map的值是否是MultipartFile类型
                    if (map.values().stream().allMatch(MultipartFile.class::isInstance)) {
                        // 数据处理
                        for (Object key : map.keySet()) {
                            Map<Object, String> outMap = new HashMap<>();
                            outMap.put(key, "二进制文件");
                            ars.add(outMap);
                        }
                        // 不做处理
                        continue;
                    }
                }
                ars.add(o);
            }
        }
        log.info("|====> ip: {} {} {} body = {}", ip, request.getMethod(), BaseUtil.isEmpty(query) ? uri : uri + "?" + query, new JsonMapper().registerModule(new JavaTimeModule()).writeValueAsString(ars));
    }

    /**
     * 统一日志生成
     * @param point 切入点对象
     * @param uLog 日志注解
     * @return Object
     */
    @Order(2)
    @Around("checkUserIdPointcut() && @annotation(uLog)")
    public Object action(ProceedingJoinPoint point, ULog uLog) throws Throwable {
        RequestHolder.Value value = RequestHolder.get();
        OptionLog log = new OptionLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || value == null || value.getUser() == null
                || BaseUtil.isEmpty(value.getUser().getId())) {
            // 继续执行被通知的方法
            return point.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        String path = request.getRequestURI();
        Object[] parameters = point.getArgs();
        log.setId(BaseUtil.uuid());
        log.setUserId(value.getUser().getId());
        log.setUserName(value.getUser().getRealName());
        log.setOperate(uLog.value());
        log.setStatus(1);
        log.setRelation(BaseUtil.sha256(value.getToken()));
        log.setPath(path);
        log.setParam(JSON.toJSONString(Stream.of(parameters).
                filter(o -> !isFilterParam(o)).
                collect(Collectors.toList())));
        log.setCreateTime(LocalDateTime.now());
        String tableName = createTable();
        logExecutorService.execute(() -> optionLogDao.upsert(tableName, log));
        try {
            Object result = point.proceed();
            if (!(result instanceof Result)) {
                log.setStatus(2);
                log.setResult(JSON.toJSONString(Result.ok(result)));
                return result;
            }
            Result<?> r = (Result<?>) result;
            if (StatusCode.CODE_200.eq(r.getCode())) {
                log.setStatus(2);
                log.setResult(JSON.toJSONString(result));
            } else {
                log.setStatus(3);
                log.setWrong(r.getMsg());
            }
            return result;
        } catch (Throwable e) {
            log.setStatus(3);
            log.setWrong(null == e.getMessage() ? e.getLocalizedMessage() : e.getMessage());
            throw e;
        } finally {
            logExecutorService.execute(() -> optionLogDao.upsert(tableName, log));
        }
    }

    /**
     * 创建日志消息表
     * @return 表名
     */
    private String createTable() {
        String tableName = "t_option_log_" + Year.now().getValue();
        optionLogDao.createTableIfNotExists(tableName);
        return tableName;
    }

    /**
     * 过滤不需要记录的参数
     * @param o 对象
     * @return boolean
     */
    private boolean isFilterParam(Object o) {
        return o instanceof MultipartFile ||
                o instanceof ServletRequest ||
                o instanceof ServletResponse ||
                o instanceof InputStream ||
                o instanceof OutputStream ||
                o instanceof Exception; // 避免输出堆栈信息
    }
}
