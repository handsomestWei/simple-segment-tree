package com.wjy.simple.segmenttree;

/**
 * 线段树节点
 */
public class SegmentNode<T> {

    // 节点的值
    private T data;
    // 子节点的元素在原数组索引的范围
    private int rangeLeft;
    private int rangeRight;
    // 左右子节点在线段树数组的索引
    private int leftChildIndex;
    private int rightChildIndex;
    // 节点懒惰标记值，延迟下推更新
    private T lazyData;

    public SegmentNode(T data, T defaultVal, int parentIndex, int rangeLeft, int rangeRight) {
        this.data = data;
        this.lazyData = defaultVal;
        this.rangeLeft = rangeLeft;
        this.rangeRight = rangeRight;
        this.leftChildIndex = 2 * parentIndex + 1;
        this.rightChildIndex = 2 * parentIndex + 2;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getRangeLeft() {
        return rangeLeft;
    }

    public void setRangeLeft(int rangeLeft) {
        this.rangeLeft = rangeLeft;
    }

    public int getRangeRight() {
        return rangeRight;
    }

    public void setRangeRight(int rangeRight) {
        this.rangeRight = rangeRight;
    }

    public int getLeftChildIndex() {
        return leftChildIndex;
    }

    public void setLeftChildIndex(int leftChildIndex) {
        this.leftChildIndex = leftChildIndex;
    }

    public int getRightChildIndex() {
        return rightChildIndex;
    }

    public void setRightChildIndex(int rightChildIndex) {
        this.rightChildIndex = rightChildIndex;
    }

    public T getLazyData() {
        return lazyData;
    }

    public void setLazyData(T lazyData) {
        this.lazyData = lazyData;
    }
}
