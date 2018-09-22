package com.learnNetty.netty5.case2_time;

import com.learnNetty.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Discards any incoming data.
 */
public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run(){
        //处理I/O操作的多线程事件循环器
        EventLoopGroup bossGroup = new NioEventLoopGroup();//用来接收进来的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();//用来处理已经被接收的连接

        try {
            //启动NIO服务的辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//指定接收进来的连接Channel
                    .childHandler(new ChannelInitializer<SocketChannel>() {//处理一个最近的已经接收的Channel
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)//通道实现的配置参数
                    .childOption(ChannelOption.SO_KEEPALIVE, true);//提供给由父管道ServerChannel接收到的连接


            //绑定端口,启动服务
            ChannelFuture future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server(Constant.PORT_10001).run();
    }
}
