package com.learnBase.nio.case5_datagramChannel;


import com.learnBase.Constant;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * DatagramChannel是一个能收发UDP包的通道。
 */
public class TestDatagramChannel {

    @Test
    public void udpServer() throws IOException {
        //打开 DatagramChannel
        DatagramChannel channel = DatagramChannel.open();
        channel.bind(new InetSocketAddress(Constant.ADDRESS_LOCALHOST,Constant.PORT_10002));

        /**
         * 接收数据
         * receive()方法会将接收到的数据包内容复制到指定的Buffer.
         * 注：如果Buffer容不下收到的数据，多出的数据将被丢弃。
         */
        ByteBuffer buffer = ByteBuffer.allocate(48);
        buffer.clear();
        channel.receive(buffer);

        buffer.flip();

        while (buffer.hasRemaining()){
            System.out.print((char)buffer.get());
        }
    }

    @Test
    public void udpClient() throws IOException {
        //打开 DatagramChannel
        DatagramChannel channel = DatagramChannel.open();

        //发送数据
        String msg = "hello,udp message ";
        ByteBuffer buffer = ByteBuffer.allocate(48);
        buffer.clear();
        buffer.put(msg.getBytes());
        buffer.flip();
        //如果服务端并没有监控10002端口，那么什么也不会发生。也不会通知你发出的数据包是否已收到，因为UDP在数据传送方面没有任何保证。
        channel.send(buffer,new InetSocketAddress(Constant.ADDRESS_LOCALHOST,Constant.PORT_10002));

        channel.close();
    }
}
