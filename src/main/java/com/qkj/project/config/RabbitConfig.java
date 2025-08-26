package com.qkj.project.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author KeJiang Qi
 * @date 2025/8/22 - 15:18
 * @description RabbitMQ配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix ="spring.rabbitmq")
public class RabbitConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        return factory;
    }
}
