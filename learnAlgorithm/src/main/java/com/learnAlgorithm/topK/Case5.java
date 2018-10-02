package com.learnAlgorithm.topK;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * 位图法
 */
public class Case5 {

    public static List<Integer> topKFrequent(int[] nums, int k) {

        BitSet bitSet = new BitSet();
        Map<String, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            bitSet.set(nums[i]);
            //记录数字出现的次数
            if (map.get(nums[i] + "") == null) {
                map.put(nums[i] + "", 1);
            } else {
                map.put(nums[i] + "", map.get(nums[i] + "") + 1);
            }
        }

        List<Integer> list = new ArrayList<>(k);
        int current = -1;
        for (int i = 0; i < k; i++) {
            current = bitSet.nextSetBit(current+1);
            list.add(current);
        }


        return list;
    }


    public static void main(String[] args) {
        int[] nums = {8, 13, 11, 23, 9, 2, 3};

        List<Integer> integers = Case5.topKFrequent(nums, 3);

        System.out.println(integers);
    }

}
