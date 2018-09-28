package com.learnAlgorithm.topK;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 冒k次获取Topk
 */
public class Case2 {

    public static List<Integer> topKFrequent(int[] nums, int k) {
        int i, j;
        for(i=0; i<k; i++){//表示n次排序过程。
            for(j=1; j<k-i; j++){
                if(nums[j-1] > nums[j]){//前面的数字大于后面的数字就交换
                    //交换a[j-1]和a[j]
                    int temp;
                    temp = nums[j-1];
                    nums[j-1] = nums[j];
                    nums[j]=temp;
                }
            }
        }

        List<Integer> list = Arrays.asList(ArrayUtils.toObject(nums));

        return list.subList(0,k);
    }

    public static void main(String[] args) {
        int[] nums = {8, 3, 1, 2, 5, 2, 0};

        List<Integer> integers = Case1.topKFrequent(nums, 3);

        System.out.println(integers);
    }

}
