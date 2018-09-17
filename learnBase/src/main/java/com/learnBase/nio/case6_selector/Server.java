package com.learnBase.nio.case6_selector;

import com.learnBase.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server{

    //多路复用器（管理所有的通道）
    private Selector seletor;


    public Server(int port){
        try {
            //1 打开路复用器
            this.seletor = Selector.open();
            //2 打开服务器通道
            ServerSocketChannel ssc = ServerSocketChannel.open();
            //3 设置服务器通道为非阻塞模式
            ssc.configureBlocking(false);
            //4 绑定地址
            ssc.bind(new InetSocketAddress(port));
            //5 把服务器通道注册到多路复用器上，并且监听阻塞事件
            ssc.register(this.seletor, SelectionKey.OP_ACCEPT);

            System.out.println("服务开启, 端口号:" + port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open() {
        while(true){
            try {
                //1 必须要让多路复用器开始监听
                this.seletor.select();
                //2 返回多路复用器已经选择的结果集
                Iterator<SelectionKey> keys = this.seletor.selectedKeys().iterator();
                //3 进行遍历
                while(keys.hasNext()){
                    //4 获取一个选择的元素
                    SelectionKey key = keys.next();
                    //5 直接从容器中移除就可以了
                    keys.remove();
                    //6 如果是有效的
                    if(key.isValid()){
                        //7 如果为阻塞状态
                        if(key.isAcceptable()){
                            this.accept(key);
                        }
                        //8 如果为可读状态
                        if(key.isReadable()){
                            this.read(key);
                        }
                        //9 写数据
                        if(key.isWritable()){
                            this.write(key);
                        }
                        //10 连接
                        if(key.isConnectable()){
                            this.connectable(key);
                        }

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param key
     * connectable事件
     */
    private void connectable(SelectionKey key){
        System.out.println("connectable");
    }

    /**
     * @param key
     * write事件
     */
    private void write(SelectionKey key){
        System.out.println("write");
    }

    /**
     * @param key
     * read事件
     */
    private void read(SelectionKey key) {
        try {
            //1 清空缓冲区旧的数据
            ByteBuffer readBuf = ByteBuffer.allocate(1024);

            readBuf.clear();
            //2 获取之前注册的socket通道对象
            SocketChannel sc = (SocketChannel) key.channel();
            //3 读取数据
            int count = sc.read(readBuf);
            //4 如果没有数据
            if(count == -1){
                key.channel().close();
                key.cancel();
                return;
            }
            //5 有数据则进行读取 读取之前需要进行复位方法(把position 和limit进行复位)
            readBuf.flip();
            //6 根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
            byte[] bytes = new byte[readBuf.remaining()];
            //7 接收缓冲区数据
            readBuf.get(bytes);
            //8 打印结果
            String body = new String(bytes).trim();
            System.out.println("服务端接收到数据: " + body);

            // 9..可以写回给客户端数据
            readBuf.clear();
            readBuf.put(("我已经收到数据："+body).getBytes());
            readBuf.flip();

            sc.write(readBuf);

            readBuf.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param key
     * accept事件
     */
    private void accept(SelectionKey key) {
        try {
            //1 获取服务通道
            ServerSocketChannel ssc =  (ServerSocketChannel) key.channel();
            //2 执行阻塞方法
            SocketChannel sc = ssc.accept();
            //3 设置阻塞模式
            sc.configureBlocking(false);
            //4 注册到多路复用器上，并设置读取标识
            sc.register(this.seletor, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server(Constant.PORT_10009).open();
    }
}
