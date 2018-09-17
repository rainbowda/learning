package com.learnBase.nio.case4_socketAndServerSocket;

import com.learnBase.Constant;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TestSocketAndServerSocket {

    @Test
    public void socketServer() throws IOException {
        //打开 ServerSocketChannel
        ServerSocketChannel channel = ServerSocketChannel.open();

        //开启非阻塞模式
        //socketChannel.configureBlocking(false);

        channel.bind(new InetSocketAddress(Constant.PORT_10001));

        //监听新进来的连接
        System.out.println("等待客户端连接...");
        //如果是阻塞模式的话，没有新的链接进来，就会阻塞在这里
        //如果是非阻塞模式的话，没有新的链接进来，就会立马返回一个null，不会阻塞
        SocketChannel socketChannel = channel.accept();

        ByteBuffer buffer = ByteBuffer.allocate(48);
        while ((socketChannel.read(buffer)) != -1){
            buffer.flip();

            while (buffer.hasRemaining()){
                System.out.print((char)buffer.get());
            }

            buffer.clear();
        }
        System.out.println();
        System.out.println("读完了");


        //关闭 ServerSocketChannel
        channel.close();
    }

    @Test
    public void socketClient() throws IOException {
        //1.打开 SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(Constant.ADDRESS_LOCALHOST, Constant.PORT_10001));

        //开启非阻塞模式
        //socketChannel.configureBlocking(false);

        //2.从 SocketChannel 读取数据
        ByteBuffer buffer = ByteBuffer.allocate(48);

        buffer.put("hihihi".getBytes());
        buffer.flip();
        while (buffer.hasRemaining()){
            socketChannel.write(buffer);
        }


        //关闭 SocketChannel
        socketChannel.close();
    }


}
