package com.learnJvm.case1_outOfMemoryError;

import java.util.ArrayList;
import java.util.List;

/**
 * 堆溢出
 * VM Args：-Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * -Xms20m 堆的最小值
 * -Xmx20m 堆的最大值
 * -XX:+HeapDumpOnOutOfMemoryError 出现内存溢出异常时，dump除内存堆存储快照
 * 注：生成的hprof文件在learnWay文件下
 */
public class HeapOOM {

    public static void main(String[] args) {
        List<Object> list = new ArrayList<Object>();

        while (true) {
            list.add(new Object());
        }
    }
}
