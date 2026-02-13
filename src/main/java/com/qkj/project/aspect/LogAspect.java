package com.qkj.project.aspect;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qkj.project.common.RequestHolder;
import com.qkj.project.common.Result;
import com.qkj.project.common.annotations.ULog;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.entity.OptionLog;
import com.qkj.project.mq.RabbitProducer;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 日志切面处理类
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Resource
    private RabbitProducer rabbitProducer;

    @Pointcut("execution(public * com.qkj.project.controller.*.*(..))")
    public void checkUserIdPointcut() {}

    /**
     * 复用ObjectMapper（线程安全，可配置）
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * 日志记录前置通知，用于拦截Controller层记录用户的操作
     * @param joinPoint 切入点对象
     */
    @Order(1)
    @Before("checkUserIdPointcut()")
    public void check(JoinPoint joinPoint) throws JsonProcessingException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) { return; }
        HttpServletRequest request = attributes.getRequest();
        // 提取IP
        String ip = Optional.ofNullable(request.getHeader("x-forwarded-for"))
                .filter(StringUtils::isNotBlank)
                .map(s -> s.split(",")[0].trim())
                .orElseGet(request::getRemoteAddr);
        // 构建请求信息
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String fullUrl = BaseUtil.isEmpty(query) ? uri : uri + "?" + query;
        String method = request.getMethod();
        String getMethod = "GET";
        if (getMethod.equalsIgnoreCase(method)) {
            log.info("\n======> {} ip: {} url: {} \n", method, ip, fullUrl);
            return;
        }
        if (!getMethod.equals(method)) {
            List<Object> ars = new ArrayList<>();
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
                            Map<Object, String> outMap = new HashMap<>(20);
                            outMap.put(key, "二进制文件");
                            ars.add(outMap);
                        }
                    }
                }
                ars.add(o);
            }
            log.info("\n======> {} ip: {} url: {} \n body: {} \n", request.getMethod(), ip, fullUrl, OBJECT_MAPPER.writeValueAsString(ars));
        }
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
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || value == null || value.getUser() == null
                || BaseUtil.isEmpty(value.getUser().getId())) {
            return point.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        String path = request.getRequestURI();
        OptionLog optionLog = buildBaseLog(uLog, value, path);
        optionLog.setParam(safeSerializeParams(point.getArgs()));
        Object result;
        try {
            result = point.proceed();
            handleResult(optionLog, result);
            return result;
        } catch (Throwable e) {
            optionLog.setStatus(3);
            optionLog.setWrong(Optional.ofNullable(e.getMessage())
                    .orElse(e.getLocalizedMessage()));
            throw e;
        } finally {
            try {
                rabbitProducer.sendOptionLog(JSON.toJSONString(optionLog));
            } catch (Exception e) {
                log.error("OptionLog send MQ failed", e);
            }
        }
    }

    /**
     * 过滤不需要记录的参数
     * @param o 对象
     * @return boolean
     */
    private boolean isFilterParam(Object o) {
        if (o == null) { return false; }
        return o instanceof MultipartFile ||
                o instanceof ServletRequest ||
                o instanceof ServletResponse ||
                o instanceof InputStream ||
                o instanceof OutputStream ||
                o instanceof Exception; // 避免输出堆栈信息
    }

    /**
     * 构造日志对象
     * @param uLog 日志注解
     * @param value 请求参数
     * @param path 请求路径
     * @return OptionLog
     */
    private OptionLog buildBaseLog(ULog uLog, RequestHolder.Value value, String path) {
        OptionLog log = new OptionLog();
        log.setId(BaseUtil.uuid());
        log.setUserId(value.getUser().getId());
        log.setUserName(value.getUser().getRealName());
        log.setOperate(uLog.value());
        log.setStatus(1);
        log.setRelation(BaseUtil.sha256(value.getToken()));
        log.setPath(path);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 安全序列化参数
     * @param args 参数数组
     * @return 序列化后的字符串
     */
    private String safeSerializeParams(Object[] args) {
        try {
            List<Object> list = Arrays.stream(args)
                    .filter(this::isFilterParam)
                    .collect(Collectors.toList());

            return JSON.toJSONString(list);
        } catch (Exception e) {
            return "[unserializable params]";
        }
    }

    /**
    * 处理方法返回值，根据返回值设置日志状态和结果
     * @param log 日志信息
     * @param result 请求结果
     */
    private void handleResult(OptionLog log, Object result) {
        if (!(result instanceof Result)) {
            log.setStatus(2);
            log.setResult(JSON.toJSONString(Result.ok(result)));
            return;
        }
        Result<?> r = (Result<?>) result;
        if (StatusCode.CODE_200.eq(r.getCode())) {
            log.setStatus(2);
        } else {
            log.setStatus(3);
            log.setWrong(r.getMsg());
        }
        log.setResult(JSON.toJSONString(r));
    }
}
