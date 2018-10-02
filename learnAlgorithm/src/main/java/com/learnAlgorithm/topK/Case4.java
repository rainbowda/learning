package com.learnAlgorithm.topK;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 随机选择
 */
public class Case4 {

    public static List<Integer> topKFrequent(int[] nums, int k) {




        List<Integer> list = Arrays.asList(ArrayUtils.toObject(nums));
        return list.subList(0,k);
    }

    private int randommizedSelect(int[] nums, int low, int high, int k){
        if (low == high) {
            return nums[low];
        }
        //计算当前利用一次快速排序获取nums[low]在整个数组中的位置
        int i = partition(nums, low, high);

        //如果nums[low]位置小于k，说明k的元素在数组的右边，反之，则在左边
        if ((i-low) >= k){
            return randommizedSelect(nums, low,i-1,k);
        } else {
            return randommizedSelect(nums, i+1,high,k);
        }
    }

    /**
     * 一次快速排序代码（未实现）
     * @param nums
     * @param low
     * @param high
     * @return
     */
    private int partition(int[] nums, int low, int high) {
        return 0;
    }

    public static void main(String[] args) {
        int[] nums = {8, 3, 1, 2, 5, 2, 0};

        List<Integer> integers = Case1.topKFrequent(nums, 3);

        System.out.println(integers);
    }

}
