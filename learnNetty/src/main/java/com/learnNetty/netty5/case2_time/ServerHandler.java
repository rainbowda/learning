package com.learnNetty.netty5.case2_time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

    /**
     * 将会在连接被建立并且准备进行通信时被调用。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf byteBuf = ctx.alloc().buffer(4);
        byteBuf.writeInt((int) (System.currentTimeMillis() / 1000L));

        final ChannelFuture future = ctx.writeAndFlush(byteBuf).sync();

        //写请求已经完成通知
        future.addListener((ChannelFutureListener) channelFuture -> {
            assert future == channelFuture;
            System.out.println("关闭");
            ctx.close();
        });

        super.channelActive(ctx);
    }
}
