package com.wjy.simple.segmenttree;

public class SegmentTreeTest {

    public static void main(String[] args) {
        Integer[] nums = {1, 2, 3, 4};

        SegmentTree tree = new SegmentTree(nums, new IntegerMergeImpl());
        // nums[1] + nums[2]
        System.out.println(tree.search(1, 2));
        // nums = {7, 7, 7, 7}
        tree.batchUpdate(0, 3, 7);
        System.out.println(tree.search(3, 3));
        // nums[3] = 8
        tree.update(3, 8);
        System.out.println(tree.search(3, 3));
    }

}
