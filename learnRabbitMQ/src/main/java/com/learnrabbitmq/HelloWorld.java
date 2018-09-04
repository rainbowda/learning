package com.learnrabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.junit.Test;

import java.io.IOException;

public class HelloWorld extends BaseTest {

    private final static String QUEUE_NAME = "hello";

    @Test
    public void recv() throws IOException {
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" 等待信息中...");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" 接收到信息:. '" + message + "'");
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);

        System.in.read();
    }

    @Test
    public void send() throws IOException {
        String message = "Hello World!";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" 发送信息:'" + message + "'");
    }
}
