package com.learnrabbitmq;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;

public class Topics extends BaseTest {

    private static final String EXCHANGE_NAME = "topic_logs";

    @Test
    public void send() throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        /**
         * * (星号) 用来表示一个单词.
         * # (井号) 用来表示任意数量（零个或多个）单词。
         */
        String routingKey = "news.china.tourism";//国内的旅游新闻
        //String routingKey = "news.abroad.tourism";//国外的旅游新闻
        //String routingKey = "recommend.*.tourism";//国内外的旅游推荐
        //String routingKey = "news.*.tourism";//国内外的旅游新闻
        //String routingKey = "*.*.tourism";//所有的旅游信息

        String message = "Hello World!";

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" 发送信息:'" + routingKey + "':'" + message + "'");
    }

    @Test
    public void receive() throws IOException {
        String[] argv = new String[2];

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //让服务器为我们选择一个随机的队列名
        String queueName = channel.queueDeclare().getQueue();

        if (argv.length < 1) {
            System.exit(1);
        }

        for (String bindingKey : argv) {
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
        }

        System.out.println(" 等待信息中...");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" 接收到信息:. '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
