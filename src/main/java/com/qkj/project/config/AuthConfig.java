package com.qkj.project.config;

import com.qkj.project.utils.BaseUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 特殊接口过滤类
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("auth")
public class AuthConfig {
    private String ignoreUrls;
    private String queryUrls;

    /**
     * 不需要 token 校验接口
     * @param url
     * @return
     */
    public boolean ignoreUrlsContain(String url) {
        if (BaseUtil.isEmpty(ignoreUrls)) { return false; }
        String regex = ignoreUrls.replaceAll("\\*", ".*")
                .replaceAll(",", "|");
        boolean matches = Pattern.matches("^(" + regex + ")$", url);
        return matches;
    }

    /**
     * token 放在请求参数中校验接口
     * @param url
     * @return
     */
    public boolean queryUrlsContain(String url) {
        if (BaseUtil.isEmpty(queryUrls)) { return false; }
        String regex = queryUrls.replaceAll("\\*", ".*")
                .replaceAll(",", "|");
        return Pattern.matches("^("+regex+")$", url);
    }
}
