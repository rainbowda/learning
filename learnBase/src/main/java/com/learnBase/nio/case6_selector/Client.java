package com.learnBase.nio.case6_selector;

import com.learnBase.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    private InetSocketAddress inetSocketAddress;
    private ByteBuffer buf = ByteBuffer.allocate(1024);//建立缓冲区

    public Client(String address, Integer port) {
        this.inetSocketAddress = new InetSocketAddress(address, port);
    }

    public void connect() {
        try (SocketChannel sc = SocketChannel.open()) {
            //进行连接
            sc.connect(inetSocketAddress);

            while(true){
                //定义一个字节数组，然后使用系统录入功能：
                byte[] bytes = new byte[1024];
                System.out.print("请输出你要发送的内容:");
                System.in.read(bytes);

                //把数据放到缓冲区中
                buf.put(bytes);
                //对缓冲区进行复位
                buf.flip();
                //写出数据
                sc.write(buf);
                //清空缓冲区数据
                buf.clear();

                sc.read(buf);
                buf.flip();


                byte[] bytes2 = new byte[buf.remaining()];
                //7 接收缓冲区数据
                buf.get(bytes2);
                //8 打印结果
                String body = new String(bytes2).trim();
                System.out.println("服务端返回数据: " + body);

                buf.clear();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Client(Constant.ADDRESS_LOCALHOST, Constant.PORT_10009).connect();
    }
}
