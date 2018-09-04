package com.learnrabbitmq;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;

public class WorkQueues extends BaseTest {

    private static final String TASK_QUEUE_NAME = "task_queue";

    @Test
    public void newTask() throws IOException {
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

        String message = "Hello World...........!";

        channel.basicPublish("", TASK_QUEUE_NAME,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes("UTF-8"));
        System.out.println(" 发送信息:'" + message + "'");
    }

    @Test
    public void worker() throws IOException {
        //定义一个queue
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" 等待信息中...");

        //设置消费者一次只接受1条消息
        channel.basicQos(1);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                System.out.println(" 接收到信息:. '" + message + "'");
                try {
                    doWork(message);
                } finally {
                    System.out.println(" 处理结束");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(TASK_QUEUE_NAME, false, consumer);

        System.in.read();
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
