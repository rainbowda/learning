package com.learnBase.nio.case1_channelAndBuffer;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestChannelAndBuffer {
    public static void main(String[] args) {
        String filePath = TestChannelAndBuffer.class.getResource("testChannelAndBuffer.txt").getPath();

        try (RandomAccessFile file = new RandomAccessFile(filePath,"rw");
             FileChannel channel = file.getChannel()) {

            //容量为48
            ByteBuffer byteBuffer = ByteBuffer.allocate(48);

            //读取
            while (channel.read(byteBuffer) != -1){
                //设置buffer为读模式
                byteBuffer.flip();

                //是否读完
                while (byteBuffer.hasRemaining()){
                    System.out.print((char)byteBuffer.get());
                }

                //清空已经读取的部分
                byteBuffer.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
