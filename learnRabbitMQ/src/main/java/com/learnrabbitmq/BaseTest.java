package com.learnrabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BaseTest {

    protected Connection connection;
    protected Channel channel;

    @Before
    public void beforeTest() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
    }

    @After
    public void afterTest() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }
}
