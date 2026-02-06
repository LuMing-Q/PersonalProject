package com.qkj.project.mq;

import com.qkj.project.config.RabbitConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author KeJiang Qi
 * @date 2025/8/22 - 15:27
 * @description MQ测试方法
 */
@Component
public class RabbitTest implements CommandLineRunner {

    @Resource
    private RabbitConfig rabbitConfig;

    @Override
    public void run(String... args) throws Exception {
        ConnectionFactory factory = rabbitConfig.rabbitConnectionFactory();
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel(1);
             Channel channel1 = connection.createChannel(1)) {

            System.out.println("channel  = " + channel);
            System.out.println("channel1 = " + channel1);
        }
    }
}
