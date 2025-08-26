package com.qkj.project.mq;

import com.qkj.project.config.RabbitConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author KeJiang Qi
 * @date 2025/8/22 - 15:27
 * @description
 */
@Component
public class RabbitTest {

    @Resource
    private static RabbitConfig rabbitConfig;

    private static ConnectionFactory factory = rabbitConfig.rabbitConnectionFactory();

    public static void main(String[] args)throws Exception {
        // 创建连接
        try (Connection connection = factory.newConnection()) {
            // 创建通道 createChannel方法内部可以传参，表示创建的通道号，如果传参一致，后创建的则会返回同一个通道对象
            Channel channel = connection.createChannel(1);
            Channel channel1 = connection.createChannel(1);
            System.out.println("channel = " + channel);
            System.out.println("channel1 = " + channel1);
        }
    }
}
