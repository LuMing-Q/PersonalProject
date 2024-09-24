package com.qkj.project.aspect;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 日志处理类
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
     * 统一日志输出
     * @param joinPoint
     * @throws JsonProcessingException
     */
    @Order(1)
    @Before("checkUserIdPointcut()")
    public void check(JoinPoint joinPoint) throws JsonProcessingException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
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
                if (Objects.isNull(o) || o instanceof MultipartFile || o instanceof MultipartFile[] || o instanceof HttpServletRequest || o instanceof HttpServletResponse) {
                    continue;
                }
                ars.add(o);
            }
        }
        log.info("|====> ip: {} {} {} body = {}", ip, request.getMethod(), uri + "?" + query, new JsonMapper().writeValueAsString(ars));
    }

    /**
     * 统一日志生成
     * @param point
     * @param uLog
     * @return
     * @throws Throwable
     */
    @Order(2)
    @Around("checkUserIdPointcut() && @annotation(uLog)")
    public Object action(ProceedingJoinPoint point, ULog uLog) throws Throwable {
        RequestHolder.Value value = RequestHolder.get();
        OptionLog log = new OptionLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || value == null || value.getUser() == null
                || BaseUtil.isEmpty(value.getUser().getId())) { return point.proceed(); }
        HttpServletRequest request = attributes.getRequest();
        String path = request.getRequestURI();
        Object[] parameters = point.getArgs();
        log.setId(BaseUtil.uuid());
        log.setUserId(value.getUser().getId());
        log.setUserName(value.getUser().getUsername());
        log.setOperate(uLog.value());
        log.setStatus(1);
        log.setRelation(BaseUtil.sha256(value.getToken()));
        log.setPath(path);
        log.setParam(JSON.toJSONString(Stream.of(parameters).filter(o -> !(o instanceof MultipartFile)).collect(Collectors.toList())));
        logExecutorService.execute(() -> optionLogDao.upsert(log));
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
            }
            else {
                log.setStatus(3);
                log.setWrong(r.getMsg());
            }
            return result;
        }
        catch (Throwable e) {
            log.setStatus(3);
            log.setWrong(e.getMessage());
            throw e;
        }
        finally { logExecutorService.execute(() -> optionLogDao.upsert(log)); }
    }
}
