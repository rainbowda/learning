package com.learnrabbitmq;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;

/**
 * 订阅新闻，根据你订阅的类型，接收新闻
 */
public class Direct extends BaseTest {

    private static final String EXCHANGE_NAME = "direct_logs";

    @Test
    public void send() throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //String routingKey = "info";
        //String routingKey = "warning";
        String routingKey = "error";
        //String message = "info message!";
        //String message = "warning message!";
        String message = "error message!";

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" 发送信息,路由key='" + routingKey + "':'" + message + "'");
    }

    @Test
    public void receive() throws IOException {
        //String[] argv = new String[]{"info"};
        //String[] argv = new String[]{"error"};
        //String[] argv = new String[]{"warning"};
        String[] argv = new String[]{"warning","info"};

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        for(String routingKey : argv){
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
            System.out.println(" 等待"+routingKey+"信息中...");
        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" 接收到信息:. '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);

        System.in.read();
    }
}
