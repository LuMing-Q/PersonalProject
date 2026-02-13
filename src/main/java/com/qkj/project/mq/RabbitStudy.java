package com.qkj.project.mq;

import com.qkj.project.config.RabbitConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author KeJiang Qi
 * @date 2025/8/22 - 15:27
 * @description MQ测试方法
 */
@Slf4j
//@Component
public class RabbitStudy implements CommandLineRunner {

    @Resource
    private RabbitConfig rabbitConfig;

    @Override
    public void run(String... args) {
        ConnectionFactory factory = rabbitConfig.rabbitConnectionFactory();
        Channel channel;
        Channel channel1;
        Channel channel2;
        try (Connection connection = factory.newConnection()) {
            // 传参为 channelNumber    区分不同的通道，如果传参相同后一个直接返回 null
            channel = connection.createChannel(1);
            channel1 = connection.createChannel(1);
            channel2 = connection.createChannel(2);
            System.out.println("channel  = " + channel);
            System.out.println("channel1 = " + channel1);
            System.out.println("channel2 = " + channel2);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
