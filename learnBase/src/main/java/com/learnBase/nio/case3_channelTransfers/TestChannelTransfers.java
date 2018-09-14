package com.learnBase.nio.case3_channelTransfers;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class TestChannelTransfers {
    public static void main(String[] args) {
        String fromPath = TestChannelTransfers.class.getResource("fromFile.txt").getPath();
        String toPath = TestChannelTransfers.class.getResource("toFile.txt").getPath();


        try (RandomAccessFile fromFile = new RandomAccessFile(fromPath,"rw");
             RandomAccessFile toFile = new RandomAccessFile(toPath,"rw");
             FileChannel fromchannel = fromFile.getChannel();
             FileChannel toChannel = toFile.getChannel()) {

            toChannel.transferTo(0,fromchannel.size(),fromchannel);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
