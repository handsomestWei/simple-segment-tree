package com.wjy.simple.segmenttree;

/**
 * 简单的线段树实现。开4n空间，使用懒惰标记延迟下推更新 TODO 后续尝试使用DFS和链表实现2n空间
 */
public class SegmentTree<T> {

    private static final String MSG_PARAM_NULL = "param is null";
    private static final String MSG_INDEX_OUT_OF = "index is illegal";

    // 原数组
    private T[] data;
    // 原数组长度
    private int len;
    // 线段树数组
    private SegmentNode<T>[] segmentNodeList;
    // 数组操作函数
    private IMerger<T> mergerFunc;

    public SegmentTree(T[] data, IMerger<T> mergerFunc) {
        if (data == null || mergerFunc == null) {
            throw new IllegalArgumentException(MSG_PARAM_NULL);
        }
        this.data = data;
        // 数据处理函数，如求和
        this.mergerFunc = mergerFunc;
        this.len = data.length;
        // 创建原数组4倍长度的空间
        segmentNodeList = new SegmentNode[4 * len];
        buildSegmentTree(0, 0, len - 1);
    }

    // 单点更新
    public void update(int index, T data) {
        if (data == null) {
            throw new IllegalArgumentException(MSG_PARAM_NULL);
        }
        if (!checkIndex(index)) {
            throw new IllegalArgumentException(MSG_INDEX_OUT_OF);
        }
        updateSegmentTree(0, index, data);
    }

    // 范围批量更新
    public void batchUpdate(int rangeLeft, int rangeRight, T data) {
        if (data == null) {
            throw new IllegalArgumentException(MSG_PARAM_NULL);
        }
        if (!checkIndex(rangeLeft, rangeRight)) {
            throw new IllegalArgumentException(MSG_INDEX_OUT_OF);
        }
        batchUpdateSegmentTree(0, rangeLeft, rangeRight, data);
    }

    // 范围查找
    public T search(int queryLeft, int queryRight) {
        if (!checkIndex(queryLeft, queryRight)) {
            throw new IllegalArgumentException(MSG_INDEX_OUT_OF);
        }
        return search(0, queryLeft, queryRight);
    }

    // 构建线段树
    private void buildSegmentTree(int index, int rangeLeft, int rangeRight) {
        SegmentNode<T> node = segmentNodeList[index];
        if (node == null) {
            // 延迟初始化
            node =
                new SegmentNode<>(mergerFunc.getDefaultVal(), mergerFunc.getDefaultVal(), index, rangeLeft, rangeRight);
            segmentNodeList[index] = node;
        }
        if (rangeLeft == rangeRight) {
            // 递归到叶子节点
            node.setData(data[rangeLeft]);
            return;
        }
        // 计算左右子节点的边界
        int mid = (rangeLeft + rangeRight) / 2;
        // 递归左节点
        buildSegmentTree(node.getLeftChildIndex(), rangeLeft, mid);
        // 递归右节点
        buildSegmentTree(node.getRightChildIndex(), mid + 1, rangeRight);
        // 回溯，更新父节点的值
        setSegmentNodeData(index);
    }

    // 更新线段树某个节点
    private void updateSegmentTree(int index, int targetIndex, T data) {
        SegmentNode<T> node = segmentNodeList[index];
        if (node.getLazyData() != mergerFunc.getDefaultVal()) {
            // 下推，查询前先更新懒惰标记的值
            pushDown(index);
        }
        int rangeLeft = node.getRangeLeft();
        int rangeRight = node.getRangeRight();
        if (rangeLeft == rangeRight) {
            // 递归到叶子节点
            node.setData(data);
            return;
        }
        // 计算左右子节点的边界
        int mid = (rangeLeft + rangeRight) / 2;
        // 自顶向下递归
        if (targetIndex > mid) {
            // 查找右区间
            updateSegmentTree(node.getRightChildIndex(), targetIndex, data);
        } else {
            // 查找左区间
            updateSegmentTree(node.getLeftChildIndex(), targetIndex, data);
        }
        // 回溯，更新父节点的值
        setSegmentNodeData(index);
    }

    private void batchUpdateSegmentTree(int index, int rangeLeft, int rangeRight, T data) {
        SegmentNode<T> node = segmentNodeList[index];
        if (node.getRangeLeft() == rangeLeft && node.getRangeRight() == rangeRight) {
            // 在区间内，批量更新
            node.setData(mergerFunc.batchMerge(rangeLeft, rangeRight, data));
            // 叠加懒惰标记
            node.setLazyData(mergerFunc.merge(node.getLazyData(), data));
            return;
        }
        int mid = (node.getRangeLeft() + node.getRangeRight()) / 2;
        if (rangeRight <= mid) {
            // 完全在左区间
            batchUpdateSegmentTree(node.getLeftChildIndex(), rangeLeft, rangeRight, data);
        } else if (rangeLeft > mid) {
            // 完全在右区间
            batchUpdateSegmentTree(node.getRightChildIndex(), rangeLeft, rangeRight, data);
        } else {
            // 区间分解，分别往左右区间递归
            batchUpdateSegmentTree(node.getLeftChildIndex(), rangeLeft, mid, data);
            batchUpdateSegmentTree(node.getRightChildIndex(), mid + 1, rangeRight, data);
        }
        // 回溯，更新父节点的值
        setSegmentNodeData(index);
    }

    // 范围查询线段树
    private T search(int index, int queryLeft, int queryRight) {
        SegmentNode<T> node = segmentNodeList[index];
        if (node.getLazyData() != mergerFunc.getDefaultVal()) {
            // 下推，查询前先更新懒惰标记的值
            pushDown(index);
        }
        int rangeLeft = node.getRangeLeft();
        int rangeRight = node.getRangeRight();
        if (rangeLeft == queryLeft && rangeRight == queryRight) {
            return node.getData();
        }
        // 计算左右子节点的边界
        int mid = (rangeLeft + rangeRight) / 2;
        if (queryLeft > mid) {
            // 查找右区间
            return search(node.getRightChildIndex(), queryLeft, queryRight);
        } else if (queryRight <= mid) {
            // 查找左区间
            return search(node.getLeftChildIndex(), queryLeft, queryRight);
        }
        T leftData = search(node.getLeftChildIndex(), queryLeft, mid);
        T rightData = search(node.getRightChildIndex(), mid + 1, queryRight);
        return mergerFunc.merge(leftData, rightData);
    }

    // 设置节点值
    private void setSegmentNodeData(int index) {
        SegmentNode node = segmentNodeList[index];
        // 一段区间的元素和等于它的子区间的元素和
        node.setData(mergerFunc.merge(segmentNodeList[node.getLeftChildIndex()].getData(),
            segmentNodeList[node.getRightChildIndex()].getData()));
    }

    // 下推懒惰标记到左右子节点
    private void pushDown(int index) {
        SegmentNode<T> node = segmentNodeList[index];
        if (node.getRangeLeft() == node.getRangeRight()) {
            // 叶节点，标记不用下传
            node.setLazyData(mergerFunc.getDefaultVal());
            return;
        }
        // 下推到左子节点
        SegmentNode<T> leftChildNode = segmentNodeList[node.getLeftChildIndex()];
        leftChildNode.setLazyData(node.getLazyData());
        leftChildNode.setData(
            mergerFunc.batchMerge(leftChildNode.getRangeLeft(), leftChildNode.getRangeRight(), node.getLazyData()));
        // 下推到右子节点
        SegmentNode<T> rightChildNode = segmentNodeList[node.getRightChildIndex()];
        rightChildNode.setLazyData(node.getLazyData());
        rightChildNode.setData(
            mergerFunc.batchMerge(rightChildNode.getRangeLeft(), rightChildNode.getRangeRight(), node.getLazyData()));
        // 下推完成，清除父节点的延迟数据
        node.setLazyData(mergerFunc.getDefaultVal());
    }

    private boolean checkIndex(int index) {
        if (index < 0 || index > len) {
            return false;
        }
        return true;
    }

    private boolean checkIndex(int left, int right) {
        if (left < 0 || left > len || right < 0 || right > len || left > right) {
            return false;
        }
        return true;
    }

    public interface IMerger<T> {

        T merge(T t1, T t2);

        T batchMerge(int left, int right, T data);

        T getDefaultVal();
    }
}
