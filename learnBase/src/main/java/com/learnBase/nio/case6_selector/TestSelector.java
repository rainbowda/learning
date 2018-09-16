package com.learnBase.nio.case6_selector;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Selector（选择器）是Java NIO中能够检测一到多个NIO通道，
 * 并能够知晓通道是否为诸如读写事件做好准备的组件。
 * 这样，一个单独的线程可以管理多个channel，从而管理多个网络连接。
 */
public class TestSelector {

    /**
     * @throws IOException
     * 运行server方法后
     * 继续运行case5和case4的client方法
     */
    @Test
    public void server() throws IOException {
        //创建Selector
        Selector selector = Selector.open();

        //创建几个channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        DatagramChannel datagramChannel = DatagramChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(10001));
        datagramChannel.bind(new InetSocketAddress(10002));
        //与Selector一起使用时，Channel必须处于非阻塞模式下
        serverSocketChannel.configureBlocking(false);
        datagramChannel.configureBlocking(false);

        /**
         * 向Selector注册通道
         * register()方法的第二个参数,表示对Channel的什么事件感兴趣,可以监听以下四种不同类型的事件
         * Connect
         * Accept
         * Read
         * Write
         */
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        datagramChannel.register(selector,SelectionKey.OP_READ);

        while (true){
            //当select()返回值不为0时,表明有一个或更多个通道就绪了
            if (selector.select() == 0) continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();

                //ServerSocketChannel 监听了OP_ACCEPT事件
                if (key.isAcceptable()) {
                    System.out.println("监听到OP_ACCEPT事件");
                    SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
                    ByteBuffer buffer = ByteBuffer.allocate(48);
                    while ((socketChannel.read(buffer)) != -1){
                        buffer.flip();

                        while (buffer.hasRemaining()){
                            System.out.print((char)buffer.get());
                        }

                        buffer.clear();
                    }
                    System.out.println();
                    System.out.println("监听OP_ACCEPT事件结束");

                }
                //OP_CONNECT事件
                if (key.isConnectable()) {
                    System.out.println("监听到OP_CONNECT事件");
                }
                //OP_WRITE事件
                if (key.isWritable()){
                    System.out.println("监听到OP_WRITE事件");
                }

                //OP_READ OP_WRITE事件
                if (key.isReadable()) {
                    System.out.println("监听到OP_READ OP_WRITE事件");

                    ByteBuffer buffer = ByteBuffer.allocate(48);
                    buffer.clear();
                    ((DatagramChannel)key.channel()).receive(buffer);
                    buffer.flip();

                    while (buffer.hasRemaining()){
                        System.out.print((char)buffer.get());
                    }
                    System.out.println();
                    System.out.println("监听OP_READ OP_WRITE事件结束");
                }

                //Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。
                //下次该通道变成就绪时，Selector会再次将其放入已选择键集中。
                iterator.remove();
            }
        }
    }
}
