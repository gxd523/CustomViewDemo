package com.demo.customview;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by guoxiaodong on 2020/6/19 15:37
 */
public class PagerLayoutManager extends RecyclerView.LayoutManager {
    /**
     * 行数
     */
    private final int mRows;
    /**
     * 列数
     */
    private final int mColumns;
    /**
     * 一页的条目数量
     */
    private final int mPageSize;
    /**
     * 条目的显示区域
     */
    private final SparseArray<Rect> itemRectMap;
    private final OrientationType mOrientation;
    /**
     * item宽
     */
    private int mItemWidth;
    /**
     * item高
     */
    private int mItemHeight;
    /**
     * 水平滚动距离(偏移量)
     */
    private int mOffsetX;
    /**
     * 垂直滚动距离(偏移量)
     */
    private int mOffsetY;
    /**
     * 最大允许滑动的宽度
     */
    private int mMaxScrollX;
    /**
     * 最大允许滑动的高度
     */
    private int mMaxScrollY;

    public PagerLayoutManager(int rows, int columns, OrientationType orientation) {
        this.mOrientation = orientation;
        this.mRows = rows;
        this.mColumns = columns;
        mPageSize = rows * columns;
        itemRectMap = new SparseArray<>();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout() || !state.didStructureChange()) {// 如果是preLayout则不重新布局
            return;
        }

        // 计算页面数量
        int pageCount = getItemCount() / mPageSize;
        if (getItemCount() % mPageSize != 0) {
            pageCount++;
        }

        // 计算可以滚动的最大数值，并对滚动距离进行修正
        if (canScrollHorizontally()) {
            mMaxScrollX = (pageCount - 1) * getWidthWithoutPadding();
            mMaxScrollY = 0;
            if (mOffsetX > mMaxScrollX) {
                mOffsetX = mMaxScrollX;
            }
        } else {
            mMaxScrollX = 0;
            mMaxScrollY = (pageCount - 1) * getHeightWithoutPadding();
            if (mOffsetY > mMaxScrollY) {
                mOffsetY = mMaxScrollY;
            }
        }

        if (mItemWidth <= 0) {
            mItemWidth = getWidthWithoutPadding() / mColumns;
        }
        if (mItemHeight <= 0) {
            mItemHeight = getHeightWithoutPadding() / mRows;
        }
        // TODO: 2020/6/19 optimize
        for (int i = 0; i < mPageSize * 2; i++) {// 预存储两页的View显示区域
            getItemRect(i);
        }

        if (mOffsetX == 0 && mOffsetY == 0) {
            for (int i = 0; i < Math.min(getItemCount(), mPageSize); i++) {// 预存储View
                View itemView = recycler.getViewForPosition(i);
                addView(itemView);
                measureChildWithMargins(// TODO: 2020/6/20 why
                        itemView,
                        getWidthWithoutPadding() - mItemWidth,
                        getHeightWithoutPadding() - mItemHeight
                );
            }
        }

        recycleAndFillItems(recycler, state, true);
    }

    /**
     * 回收和填充布局
     *
     * @param isStart 是否从头开始，用于控制View遍历方向，true 为从头到尾，false 为从尾到头
     */
    @SuppressLint("CheckResult")
    private void recycleAndFillItems(RecyclerView.Recycler recycler, RecyclerView.State state, boolean isStart) {
        if (state.isPreLayout()) {
            return;
        }

        // 计算显示区域区前后多存储一列或则一行
        Rect displayRect = new Rect(
                mOffsetX - mItemWidth,
                mOffsetY - mItemHeight,
                mOffsetX + getWidthWithoutPadding() + mItemWidth,
                mOffsetY + getHeightWithoutPadding() + mItemHeight
        );
        // 对显显示区域进行修正(计算当前显示区域和最大显示区域对交集)
        displayRect.intersect(0, 0, mMaxScrollX + getWidthWithoutPadding(), mMaxScrollY + getHeightWithoutPadding());

        int startPosition;// 获取当前页第一个条目的Pos
        int pageIndex = getPageIndexByOffset();
        int offsetRowOrColumnCount = canScrollHorizontally() ? mRows : mColumns;
        startPosition = mPageSize * (pageIndex - 1) - offsetRowOrColumnCount;
        if (startPosition < 0) {
            startPosition = 0;
        }
        int stopPosition = mPageSize * (pageIndex + 1) + offsetRowOrColumnCount;
        if (stopPosition > getItemCount()) {
            stopPosition = getItemCount();
        }

        detachAndScrapAttachedViews(recycler);// 移除所有View

        if (isStart) {
            for (int i = startPosition; i < stopPosition; i++) {
                addOrRemove(recycler, displayRect, i);
            }
        } else {
            for (int i = stopPosition - 1; i >= startPosition; i--) {
                addOrRemove(recycler, displayRect, i);
            }
        }
    }

    /**
     * 添加或者移除条目
     *
     * @param recycler    RecyclerView
     * @param displayRect 显示区域
     * @param position    条目下标
     */
    private void addOrRemove(RecyclerView.Recycler recycler, Rect displayRect, int position) {
        View itemView = recycler.getViewForPosition(position);
        Rect rect = getItemRect(position);
        if (!Rect.intersects(displayRect, rect)) {// 如果该position的item与显示区域有交集，则回收入暂存区
            removeAndRecycleView(itemView, recycler);
        } else {
            addView(itemView);
            measureChildWithMargins(
                    itemView,
                    getWidthWithoutPadding() - mItemWidth,
                    getHeightWithoutPadding() - mItemHeight
            );
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            layoutDecorated(
                    itemView,
                    rect.left - mOffsetX + params.leftMargin + getPaddingLeft(),
                    rect.top - mOffsetY + params.topMargin + getPaddingTop(),
                    rect.right - mOffsetX - params.rightMargin + getPaddingLeft(),
                    rect.bottom - mOffsetY - params.bottomMargin + getPaddingTop()
            );
        }
    }

    /**
     * 获取item显示区域
     *
     * @param position 位置下标(从0开始)
     * @return 显示区域
     */
    private Rect getItemRect(int position) {
        Rect rect = itemRectMap.get(position);
        if (null == rect) {// 计算显示区域Rect
            rect = new Rect();
            // 1、计算当前item页面偏移量
            int page = position / mPageSize;
            int offsetX = 0;
            int offsetY = 0;
            if (canScrollHorizontally()) {
                offsetX += getWidthWithoutPadding() * page;
            } else {
                offsetY += getHeightWithoutPadding() * page;
            }
            // 2、计算当前item页内偏移量
            int itemCount = position % mPageSize;// item所在页面的item个数
            int row = itemCount / mColumns;// 获取所在行
            int col = itemCount % mColumns;// 获取所在列

            offsetX += col * mItemWidth;
            offsetY += row * mItemHeight;

            rect.left = offsetX;
            rect.top = offsetY;
            rect.right = offsetX + mItemWidth;
            rect.bottom = offsetY + mItemHeight;

            itemRectMap.put(position, rect);
        }
        return rect;
    }

    /**
     * 根据offset获取页面Index
     *
     * @return 页面 Index
     */
    private int getPageIndexByOffset() {
        int pageIndex;
        if (canScrollVertically()) {
            int pageHeight = getHeightWithoutPadding();
            if (mOffsetY <= 0 || pageHeight <= 0) {
                pageIndex = 0;
            } else {
                pageIndex = mOffsetY / pageHeight;
                if (mOffsetY % pageHeight > 0) {
                    pageIndex++;
                }
            }
        } else {
            int pageWidth = getWidthWithoutPadding();
            if (mOffsetX <= 0 || pageWidth <= 0) {
                pageIndex = 0;
            } else {
                pageIndex = mOffsetX / pageWidth;
                if (mOffsetX % pageWidth > 0) {
                    pageIndex++;
                }
            }
        }
        return pageIndex;
    }

    /**
     * 水平滚动
     *
     * @param dx       滚动距离
     * @param recycler 回收器
     * @param state    滚动状态
     * @return 实际滚动距离
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int newX = mOffsetX + dx;
        int result = dx;
        if (newX > mMaxScrollX) {
            result = mMaxScrollX - mOffsetX;
        } else if (newX < 0) {
            result = -mOffsetX;
        }
        mOffsetX += result;
//        setPageIndex(getPageIndexByOffset(), true);
        offsetChildrenHorizontal(-result);
        return result;
    }

    /**
     * 垂直滚动
     *
     * @param dy       滚动距离
     * @param recycler 回收器
     * @param state    滚动状态
     * @return 实际滚动距离
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int newY = mOffsetY + dy;
        int result = dy;
        if (newY > mMaxScrollY) {
            result = mMaxScrollY - mOffsetY;
        } else if (newY < 0) {
            result = -mOffsetY;
        }
        mOffsetY += result;
//        setPageIndex(getPageIndexByOffset(), true);
        offsetChildrenVertical(-result);
        if (result > 0) {
            recycleAndFillItems(recycler, state, true);
        } else {
            recycleAndFillItems(recycler, state, false);
        }
        return result;
    }

    private int getWidthWithoutPadding() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getHeightWithoutPadding() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == OrientationType.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == OrientationType.VERTICAL;
    }

    public enum OrientationType {
        VERTICAL, HORIZONTAL
    }
}
