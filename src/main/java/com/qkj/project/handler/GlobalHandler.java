package com.qkj.project.handler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.qkj.project.common.Result;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.UnexpectedTypeException;
import java.sql.SQLSyntaxErrorException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author KeJiang Qi
 * @date 2025/2/21 - 16:23
 * @description Controller接口方法统一响应处理、全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalHandler implements ResponseBodyAdvice<Object> {

    /**
     * 方法的参数验证失败拦截 eg: @NotNull，@Size，@Min，@Max
     * @param ex ex 异常信息
     * @return Result<String> 返回结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException ex) {
        log.error("parameter error: {}", ex.getMessage());
        List<FieldError> fe = ex.getBindingResult().getFieldErrors();
        return Result.of(StatusCode.CODE_400.getCode(), "参数校验失败",
                fe.stream().map(FieldError::getField).collect(Collectors.joining(",")));
    }

    /**
     * 验证器使用错误 eg: @NotBlank 注解加在 java.lang.Long 类型数据做校验
     * @param ex ex 异常信息
     * @return Result<String> 返回结果
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public Result<String> unexpectedTypeExceptionHandle(UnexpectedTypeException ex) {
        log.error("parameter type error: {}", ex.getMessage());
        return Result.of(StatusCode.CODE_400.getCode(), "参数类型异常", ex.getMessage());
    }

    /**
     * 请求方法中缺少必需的请求参数
     * @param ex ex 异常信息
     * @return Result<String> 返回结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> missingServletRequestParameterExceptionHandle(MissingServletRequestParameterException ex) {
        log.error("parameter missing: {}", ex.getMessage());
        return Result.of(StatusCode.CODE_400.getCode(), "缺少必要的请求参数", ex.getParameterName());
    }

    /**
     * HTTP 消息不可读 客户端发送的数据时，数据的格式不正确或者无法被解析
     * @param ex ex 异常信息
     * @return Result<String> 返回结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> httpMessageNotReadableExceptionHandle(HttpMessageNotReadableException ex) {
        log.error("parameter format error: {}", ex.getMessage());
        if (ex.getRootCause() instanceof MismatchedInputException) {
            List<JsonMappingException.Reference> paths = ((MismatchedInputException) ex.getRootCause()).getPath();
            return Result.of(StatusCode.CODE_400.getCode(), "参数类型异常",
                    paths.stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining(",")));
        }
        return Result.of(StatusCode.CODE_400.getCode(), "参数类型异常", ex.getMessage());
    }

    /**
     *  HTTP 方法不支持当前的请求映射 eg:客户端使用 POST 方法访问一个只支持 GET 方法的接口
     * @param ex ex 异常信息
     * @return Result<String> 返回结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> httpRequestMethodNotSupportedExceptionHandle(HttpRequestMethodNotSupportedException ex) {
        log.error("method not support: {}", ex.getMessage());
        return Result.of(StatusCode.CODE_404.getCode(), "不存在的API或请求方法不支持", ex.getMessage());
    }

    /**
     * 自定义异常处理
     * @param ex ex 异常对象，包含自定义的业务错误信息
     * @return Result<String> 包含自定义业务错误信息的响应对象。如果捕获到的是 StatusCode.CODE_401，
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> businessExceptionHandle(BusinessException ex) {
        if (StatusCode.CODE_401.eq(ex.getCode())) {
            return Result.of(ex.getCode(), "登录已过期，请重新登录", ex.getMsg());
        }
        return Result.of(ex.getCode(), ex.getMessage(), ex.getMsg());
    }

    /**
     * 异步请求操作超时
     * @param ex ex 异常对象，包含超时操作的详细信息
     * @return Result<String> 包含超时信息和错误代码的响应对象。如果捕获到的是 AsyncRequestTimeoutException，
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Result<String> asyncRequestTimeoutExceptionHandle(AsyncRequestTimeoutException ex) {
        log.warn("Monitor event timeout: {}", ex.getMessage());
        return Result.of(StatusCode.CODE_412.getCode(), "异步监控已断开", ex.getMessage());
    }

    /**
     * 请求参数缺失
     * @param ex ex 异常对象，包含缺失的请求部分的信息
     * @return Result<String> 包含缺失的请求部分名称和错误信息的响应对象。
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Result<String> missingServletRequestPartExceptionHandle(MissingServletRequestPartException ex) {
        log.error("request part missing: {}", ex.getMessage());
        return Result.of(StatusCode.CODE_400.getCode(), "缺少请求部分", ex.getRequestPartName());
    }

    /**
     * 全局异常处理
     * @param ex 异常对象，包含全局捕获的异常信息
     * @return Result<String> 包含错误信息的响应对象，其中包含了异常的详细信息和状态码。如果捕获到的是SQLSyntaxErrorException
     */
    @ExceptionHandler(Exception.class)
    public Result<String> otherExceptionHandle(Exception ex) {
        log.error("server error: {}", ex.getMessage(), ex);
        Throwable cause = ex;
        while (cause != null) {
            // 查询告警消息表是否存在，如果不存在则返回 404 错误码和相应信息
            if (isMissingOptionLogTable(cause)) {
                return Result.of(StatusCode.CODE_404.getCode(),
                        "当前年份暂无数据，请切换其他年份",
                        cause.getMessage());
            }
            cause = cause.getCause();
        }
        return Result.of(StatusCode.CODE_500.getCode(), "服务异常", ex.getMessage());
    }

    private boolean isMissingOptionLogTable(Throwable cause) {
        if (!(cause instanceof SQLSyntaxErrorException)) {
            return false;
        }
        String msg = cause.getMessage();
        return msg != null &&
                msg.contains("Table 'qkj_project.t_option_log_") &&
                msg.contains("doesn't exist");
    }

    /**
     * 确定当前的 ResponseBodyAdvice 是否适用于给定的Controller方法的返回类型和媒体类型
     * @param returnType Controller方法的返回类型信息
     * @param converterType 将Controller的返回值转换为特定格式（例如JSON或XML）并写入响应
     * @return true 如果当前的 ResponseBodyAdvice 适用于给定的Controller方法的返回类型和媒体类型，否则为 false
     */
    @Override
    public boolean supports(@Nullable MethodParameter returnType,
                            @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 统一响应
     * @param body Controller方法返回的响应体对象
     * @param returnType Controller方法的返回类型信息
     * @param selectedContentType 当前请求的媒体类型（如application/json、text/html等）
     * @param selectedConverterType 将Controller的返回值转换为特定格式（例如JSON或XML）并写入响应
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return 处理后的响应体对象，可以是原始的body或者经过包装的Result对象
     */
    @Override
    public Object beforeBodyWrite(Object body, @Nullable MethodParameter returnType, @Nullable MediaType selectedContentType,
                                  @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response) {
        if (body instanceof LinkedHashMap) {
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) body;
            Object statusObj = map.get("status");
            Object errorObj = map.get("error");
            Object pathObj = map.get("path");
            Integer status = statusObj != null ? Integer.valueOf(statusObj.toString()) : null;
            String error = errorObj != null ? errorObj.toString() : "";
            String path = pathObj != null ? pathObj.toString() : "";
            if (status != null) {
                return Result.of(status, "请求失败", error + "  " + path);
            } else {
                return Result.ok(body);
            }
        }
        /*
         * 当 Controller的方法返回值类型为 String 时报错原因
         * HttpMessageConverter使用的是 StringHttpMessageConverter，如果将响应体包装为Result后，自然会报java.lang.ClassCastException异常
         */
        if (body instanceof String){
            return JSON.toJSONString(Result.ok(body));
        }
        if (body instanceof Result){
            return body;
        }
        return Result.ok(body);
    }
}
