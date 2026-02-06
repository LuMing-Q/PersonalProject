package com.qkj.project.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author KeJiang Qi
 * @date 2025/7/28 - 17:59
 * @description RabbitMQ配置类
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue reportQueue() {
        return new Queue("report.generate.queue", true);
    }

    @Bean
    public DirectExchange reportExchange() {
        return new DirectExchange("report.exchange");
    }

    @Bean
    public Binding bindingReportQueue() {
        return BindingBuilder.bind(reportQueue())
                .to(reportExchange())
                .with("report.generate");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // 可选：禁用时间戳
        // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }
}
