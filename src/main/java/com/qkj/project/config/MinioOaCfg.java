package com.qkj.project.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.ToString;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description minio配置类
 */
@Data
@ToString
@Configuration
@ConfigurationProperties(prefix ="unify.minio-oa")
public class MinioOaCfg {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String  bucketName;

    @Bean("minio-oa-config")
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .httpClient(new OkHttpClient.Builder()
                        .connectTimeout(600, TimeUnit.SECONDS)
                        .build())
                .build();
    }
}
