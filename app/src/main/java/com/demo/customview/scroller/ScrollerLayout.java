package com.demo.customview.scroller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollerLayout extends ViewGroup {
    private final Scroller mScroller;
    /**
     * 判定为拖动的最小移动像素数
     */
    private final int mTouchSlop;
    private float downX;
    private float moveX;
    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private float preMoveX;
    /**
     * 界面可滚动的左边界
     */
    private int leftBorder;
    /**
     * 界面可滚动的右边界
     */
    private int rightBorder;

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) {
            return;
        }
        int currentWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(currentWidth, 0, currentWidth + child.getMeasuredWidth(), child.getMeasuredHeight());
            currentWidth += child.getMeasuredWidth();
        }
        // 初始化左右边界值
        leftBorder = getChildAt(0).getLeft();
        rightBorder = getChildAt(getChildCount() - 1).getRight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX();
                preMoveX = downX;
                Log.d("gxd", "onInterceptTouchEvent.ACTION_DOWN = " + preMoveX);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = ev.getRawX();
                preMoveX = moveX;
                Log.d("gxd", "onInterceptTouchEvent.ACTION_MOVE = " + preMoveX);
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (Math.abs(moveX - downX) > mTouchSlop) {
                    Log.d("gxd", ".............");
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                moveX = event.getRawX();
                Log.d("gxd", "ACTION_MOVE = " + preMoveX + "..." + moveX + "...." + getScrollX());
                int scrolledX = (int) (preMoveX - moveX);
                if (getScrollX() + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (getScrollX() + getWidth() + scrolledX > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                }
                scrollBy(scrolledX, 0);
                preMoveX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                int targetIndex = (getScrollX() + getWidth() / 2) / getWidth();
                int dx = targetIndex * getWidth() - getScrollX();
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                mScroller.startScroll(getScrollX(), 0, dx, 0);// scrollBy(dx, 0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}

