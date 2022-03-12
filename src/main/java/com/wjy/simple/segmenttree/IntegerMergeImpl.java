package com.wjy.simple.segmenttree;

public class IntegerMergeImpl implements SegmentTree.IMerger<Integer> {

    @Override
    public Integer merge(Integer t1, Integer t2) {
        return t1 + t2;
    }

    @Override
    public Integer batchMerge(int left, int right, Integer data) {
        return (right - left + 1) * data;
    }

    @Override
    public Integer getDefaultVal() {
        return 0;
    }
}
