package com.qkj.project.config;

import com.alibaba.fastjson.JSON;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 当前线程相关信息
 */
@Configuration
@EnableAspectJAutoProxy
public class BaseConfig {

    /**
     * 不能发起 https 请求，可以传输文件
     * @return
     */
    @Bean("httpRestTemplate")
    public RestTemplate fileRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setBufferRequestBody(false);
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }

    /**
     * 用于发起 https 请求，不能传输文件
     * @return
     * @throws Exception
     */
    @Bean("httpsRestTemplate")
    public RestTemplate restTemplate() throws Exception {
        TrustStrategy acceptingTrustStrategy = ((x509Certificates, authType) -> true);
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }

    /**
     * 日志生成线程池<br>
     * <img src="https://pic.imgdb.cn/item/67243c68d29ded1a8ce6fab9.png">
     * @return
     */
    @Bean("logExecutorService")
    public ExecutorService logExecutorService() {
        return Executors.newFixedThreadPool(4);
    }

    /**
     * 配置Redis模板，使用自定义序列化器
     *
     * @param factory Redis连接工厂，用于创建Redis连接
     * @return 配置好的Redis模板实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        RedisSerializer<Object> redisSerializer = new RedisSerializer<Object>() {
            /**
             * 将对象序列化为字节数组
             *
             * @param t 要序列化的对象
             * @return 序列化后的字节数组
             * @throws SerializationException 序列化异常
             */
            @Override
            public byte[] serialize(Object t) throws SerializationException {
                if (t == null) {
                    return ("").getBytes(StandardCharsets.UTF_8);
                }
                if (t instanceof String) {
                    return ((String) t).getBytes(StandardCharsets.UTF_8);
                }
                return JSON.toJSONString(t).getBytes(StandardCharsets.UTF_8);
            }

            /**
             * 将字节数组反序列化为字符串
             *
             * @param bytes 要反序列化的字节数组
             * @return 反序列化后的字符串
             * @throws SerializationException 反序列化异常
             */
            @Override
            public String deserialize(byte[] bytes) throws SerializationException {
                return (bytes == null ? null : new String(bytes, StandardCharsets.UTF_8));
            }
        };
        template.setValueSerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        return template;
    }
}
