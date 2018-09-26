package com.learnAlgorithm.topK;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 将数组进行排序
 * 取出前k个数
 */
public class Case1 {

    public static List<Integer> topKFrequent(int[] nums, int k) {

        Arrays.sort(nums);//使用jdk内置的双轴快速排序

        /**
         * 此处有个坑,使用Arrays.asList将基本类型的数组转换为所对应的封装类型的list
         * 原文链接：https://mlangc.wordpress.com/2010/05/01/be-carefull-when-converting-java-arrays-to-lists/
         * 译文链接： http://www.importnew.com/14996.html
         */
        //List list = Arrays.asList(nums);

        //采用apache lang的ArrayUtils工具包
        List<Integer> list = Arrays.asList(ArrayUtils.toObject(nums));

        return list.subList(0,k);
    }

    public static void main(String[] args) {
        int[] nums = {8, 3, 1, 2, 5, 2, 0};

        List<Integer> integers = Case1.topKFrequent(nums, 3);

        System.out.println(integers);
    }


}
