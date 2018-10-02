package com.learnAlgorithm.topK;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 堆
 */
public class Case3 {

    public static List<Integer> topKFrequent(int[] nums, int k) {

        //使用小顶堆进行排序


        List<Integer> list = Arrays.asList(ArrayUtils.toObject(nums));
        return list.subList(0,k);
    }

    public static void main(String[] args) {
        int[] nums = {8, 3, 1, 2, 5, 2, 0};

        List<Integer> integers = Case3.topKFrequent(nums, 3);

        System.out.println(integers);
    }

}
