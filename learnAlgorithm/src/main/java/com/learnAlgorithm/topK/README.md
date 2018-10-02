## 问题描述：
从arr[1, n]这n个数中，找出最大的k个数，这就是经典的TopK问题。

## 案例：
从arr[1, 12]={5,3,7,1,8,2,9,4,7,2,6,6} 这n=12个数中，找出最大的k=5个。 

> 9、8、7、7、6

## 解决思路

首先，我们脑袋最先想到的是现将这个数组进行排序，然后在取得k个数即可。

### 方案一：排序

将数组进行从大到小排序后，获取k个数

```java
public static List<Integer> topKFrequent(int[] nums, int k) {
    Arrays.sort(nums);//使用jdk内置的双轴快速排序

    //采用apache lang的ArrayUtils工具包
    List<Integer> list = Arrays.asList(ArrayUtils.toObject(nums));

    return list.subList(0,k);
}
```

但是仔细分析下，我们其实只需要k个数，所以这里可以将全局排序优化为部分排序。

那么如何优化呢？想想最基本的算法中是如何排序的？

例如快速排序是将一组数字划分为2，那么是否可以直接找到topk中末尾的那个数，然后只需要获取一边的数据呢？

再比如冒泡排序，是否可以优化为冒k次泡？



### 方案二：冒k次泡进行局部排序
```java
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
```

既然我们将排序优化为局部排序了，那么是否可以不需要排序呢？
### 方案三：堆
先用前k个元素生成一个小顶堆，这个小顶堆用于存储当前最大的k个元素。
接着，从第k+1个元素开始扫描，和堆顶（堆中最小的元素）比较，如果被扫描的元素大于堆顶，则替换堆顶的元素，并调整堆，以保证堆内的k个元素，总是当前最大的k个元素。
直到，扫描完所有n-k个元素，最终堆中的k个元素，就是我们要的TopK。


### 方案四：随机选择
随机选择也就是寻找第k个元素的位置，然后在进行一次快速排序即可
1. 利用第一次分治将整体分为两部分
2. 查看第一次分治的partition位置是否等于k
3. 如果小于k，说明k的元素在数组的右边，反之，则在左边
4. 找到k以后以k为中心进行快速排序

### 方案五：bitmap位图法


### 延伸

> 从20亿个数字的文本中，找出最大的前100个。 