package com.learnNetty.netty5.case2_time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg; // (1)

        try {
            long time = m.readUnsignedInt() * 1000L;
            System.out.println("时间:" + time);
            ctx.close();
        } finally {
            m.release();
        }

    }
}
