package com.demo.customview.pager;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by guoxiaodong on 2020/6/19 15:37
 */
public class PagerLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {
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
    private final OrientationType mOrientation;
    /**
     * 条目的显示区域
     */
    private final SparseArray<Rect> itemRectMap;
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
    /**
     * 当前页面下标
     */
    private int pageIndex = -1;
    private RecyclerView mRecyclerView;

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

        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            // 页面变化回调
            setPageIndex(0, false);
            return;
        } else {
            setPageIndex(getPageIndexByOffset(), false);
        }

        // 计算页面数量
        int pageCount = getPageCount();

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

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (state.isPreLayout()) {
            return;
        }
        setPageIndex(getPageIndexByOffset(), false);
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
        // 对显显示区域进行修正，不超过最大显示范围(计算当前显示区域和最大显示区域对交集)
        displayRect.intersect(0, 0, mMaxScrollX + getWidthWithoutPadding(), mMaxScrollY + getHeightWithoutPadding());

        int startPosition;// 获取当前页第一个条目的Pos
        int pageIndex = getPageIndexByOffset();
        startPosition = mPageSize * (pageIndex - 2);
        if (startPosition < 0) {
            startPosition = 0;
        }
        int stopPosition = mPageSize * (pageIndex + 2);
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
    int getPageIndexByOffset() {
        int pageIndex;
        if (canScrollVertically()) {
            int pageHeight = getHeightWithoutPadding();
            if (mOffsetY <= 0 || pageHeight <= 0) {
                pageIndex = 0;
            } else {
                pageIndex = mOffsetY / pageHeight;
                if (mOffsetY % pageHeight > pageHeight / 2) {
                    pageIndex++;
                }
            }
        } else {
            int pageWidth = getWidthWithoutPadding();
            if (mOffsetX <= 0 || pageWidth <= 0) {
                pageIndex = 0;
            } else {
                pageIndex = mOffsetX / pageWidth;
                if (mOffsetX % pageWidth > pageWidth / 2) {
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
        setPageIndex(getPageIndexByOffset(), true);
        offsetChildrenHorizontal(-result);
        return result;
    }

    /**
     * 垂直滚动
     *
     * @param dy       滚动距离(上正下负)
     * @param recycler 回收器
     * @param state    滚动状态
     * @return 实际滚动距离
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int newY = mOffsetY + dy;
        int result = dy;
        if (newY > mMaxScrollY) {// 往上滚动到底
            result = mMaxScrollY - mOffsetY;
        } else if (newY < 0) {// 往下滚动到顶
            result = 0 - mOffsetY;
        }
        mOffsetY += result;
        setPageIndex(getPageIndexByOffset(), true);
        offsetChildrenVertical(-result);
        recycleAndFillItems(recycler, state, result > 0);
        return result;
    }

    @Override
    public void scrollToPosition(int position) {
        int pageIndex = position / mPageSize;
        scrollToPage(pageIndex);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        int targetPageIndex = position / mPageSize;
        smoothScrollToPage(targetPageIndex);
    }

    /**
     * 平滑滚动到指定页面
     *
     * @param pageIndex 页面下标
     */
    public void smoothScrollToPage(int pageIndex) {
        if (null == mRecyclerView || pageIndex < 0 || pageIndex >= getPageCount()) {
            return;
        }
        // 如果滚动到页面之间距离过大，先直接滚动到目标页面到临近页面，在使用 smoothScroll 最终滚动到目标
        // 否则在滚动距离很大时，会导致滚动耗费的时间非常长
        int currentPageIndex = getPageIndexByOffset();
        if (Math.abs(pageIndex - currentPageIndex) > 3) {
            if (pageIndex > currentPageIndex) {
                scrollToPage(pageIndex - 3);
            } else if (pageIndex < currentPageIndex) {
                scrollToPage(pageIndex + 3);
            }
        }

        // 具体执行滚动
        LinearSmoothScroller smoothScroller = new PagerSmoothScroller(mRecyclerView);
        int position = pageIndex * mPageSize;
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    /**
     * 滚动到指定页面
     *
     * @param pageIndex 页面下标
     */
    public void scrollToPage(int pageIndex) {
        if (null == mRecyclerView || pageIndex < 0 || pageIndex >= getPageCount()) {
            return;
        }

        int mTargetOffsetXBy;
        int mTargetOffsetYBy;
        if (canScrollVertically()) {
            mTargetOffsetXBy = 0;
            mTargetOffsetYBy = pageIndex * getHeightWithoutPadding() - mOffsetY;
        } else {
            mTargetOffsetXBy = pageIndex * getWidthWithoutPadding() - mOffsetX;
            mTargetOffsetYBy = 0;
        }
        mRecyclerView.scrollBy(mTargetOffsetXBy, mTargetOffsetYBy);
        setPageIndex(pageIndex, false);
    }

    /**
     * 监听滚动状态，滚动结束后通知当前选中的页面
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            setPageIndex(getPageIndexByOffset(), false);
        }
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

    /**
     * 计算到目标位置需要滚动的距离
     * {@link RecyclerView.SmoothScroller.ScrollVectorProvider}
     *
     * @param targetPosition 目标控件的位置
     * @return 需要滚动的距离
     */
    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        PointF vector = new PointF();
        int[] offset = getSnapOffset(targetPosition);
        vector.x = offset[0];
        vector.y = offset[1];
        return vector;
    }

    /**
     * 获取Item页内偏移量
     * 为{@link MyPagerSnapHelper}准备，用于分页滚动，确定需要滚动的距离。
     *
     * @param snapPosition 需要对齐的ItemView(对于分页布局就是页面第一个)的position
     */
    int[] getSnapOffset(int snapPosition) {
        int[] offset = new int[2];
        int[] pos = getPageXyByItemPos(snapPosition);
        offset[0] = pos[0] - mOffsetX;
        offset[1] = pos[1] - mOffsetY;
        return offset;
    }

    /**
     * 根据Item的position获取该Item所在页面的左上角x、y坐标
     *
     * @return 左上角x、y坐标
     */
    private int[] getPageXyByItemPos(int position) {
        int[] leftTop = new int[2];
        int page = position / mPageSize;
        if (canScrollHorizontally()) {
            leftTop[0] = page * getWidthWithoutPadding();
            leftTop[1] = 0;
        } else {
            leftTop[0] = 0;
            leftTop[1] = page * getHeightWithoutPadding();
        }
        return leftTop;
    }

    /**
     * @return 需要对齐的ItemView(每页的第一个View)
     */
    public View findSnapView() {
        if (getChildCount() <= 0) {
            return null;
        }
        if (null != getFocusedChild()) {// TV适配
            return getFocusedChild();
        }
        int targetPos = getPageIndexByOffset() * mPageSize;// 目标Pos
        for (int i = 0; i < getChildCount(); i++) {
            View itemView = getChildAt(i);
            if (itemView != null) {
                int childPos = getPosition(itemView);
                if (childPos == targetPos) {
                    return itemView;
                }
            }
        }
        return getChildAt(0);
    }

    /**
     * 设置当前选中页面
     *
     * @param pageIndex   页面下标
     * @param isScrolling 是否处于滚动状态
     */
    private void setPageIndex(int pageIndex, boolean isScrolling) {
        if (pageIndex == this.pageIndex) {
            return;
        }
        if (true) {// 如果允许连续滚动，那么在滚动过程中就会更新页码记录
            this.pageIndex = pageIndex;
        } else {// 否则，只有等滚动停下时才会更新页码记录
            if (!isScrolling) {
                this.pageIndex = pageIndex;
            }
        }
//        if (isScrolling && !mChangeSelectInScrolling) {
//            return;
//        }
        if (pageIndex >= 0) {
//            if (null != mPageListener) {
//                mPageListener.onPageSelect(pageIndex);
//            }
        }
    }

    /**
     * @return 下一页第一个Item的position
     */
    int findNextPageFirstItem() {
        int nextPage = pageIndex;
        nextPage++;
        if (nextPage >= getPageCount()) {
            nextPage = getPageCount() - 1;
        }
        return nextPage * mPageSize;
    }

    /**
     * @return 上一页的第一个Item的position
     */
    int findPrePageFirstItem() {
        int previousPage = pageIndex;
        previousPage--;
        if (previousPage < 0) {
            previousPage = 0;
        }
        return previousPage * mPageSize;
    }

    /**
     * 获取总页数
     */
    private int getPageCount() {
        int pageCount = getItemCount() / mPageSize;
        if (getItemCount() % mPageSize != 0) {
            pageCount++;
        }
        return pageCount;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRecyclerView = view;
    }

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect,
                                                 boolean immediate, boolean focusedChildVisible) {
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }

    boolean isKeyToNextOrPrePage(int keycode) {
        View snapView = findSnapView();
        int position = getPosition(snapView);
        return keycode == KeyEvent.KEYCODE_DPAD_UP && position % mPageSize < mColumns
                || keycode == KeyEvent.KEYCODE_DPAD_DOWN && position % mPageSize >= mColumns * 2;
    }

    public enum OrientationType {
        VERTICAL, HORIZONTAL
    }
}
