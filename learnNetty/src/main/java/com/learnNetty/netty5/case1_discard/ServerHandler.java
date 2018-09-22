package com.learnNetty.netty5.case1_discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class ServerHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf in = (ByteBuf) msg;
            System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
        } finally {
            //忽略收到的数据
            //((ByteBuf) msg).release();
            ReferenceCountUtil.release(msg);
        }
    }
}
