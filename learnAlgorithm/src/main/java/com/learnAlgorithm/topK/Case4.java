package com.learnAlgorithm.topK;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 随机选择
 */
public class Case4 {

    public static List<Integer> topKFrequent(int[] nums, int k) {

        //使用小


        List<Integer> list = Arrays.asList(ArrayUtils.toObject(nums));
        return list.subList(0,k);
    }

    public static void main(String[] args) {
        int[] nums = {8, 3, 1, 2, 5, 2, 0};

        List<Integer> integers = Case1.topKFrequent(nums, 3);

        System.out.println(integers);
    }

}
