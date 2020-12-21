package com.demo.customview.scroller

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import kotlin.math.abs

class ScrollerLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val mScroller: Scroller = Scroller(context)

    /**
     * 判定为拖动的最小移动像素数
     */
    private val mTouchSlop by lazy {
        val configuration = ViewConfiguration.get(context)
        configuration.scaledPagingTouchSlop
    }

    private var downX = 0f
    private var moveX = 0f

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private var preMoveX = 0f

    /**
     * 界面可滚动的左边界
     */
    private var leftBorder = 0

    /**
     * 界面可滚动的右边界
     */
    private var rightBorder = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!changed) {
            return
        }
        var currentWidth = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(currentWidth, 0, currentWidth + child.measuredWidth, child.measuredHeight)
            currentWidth += child.measuredWidth
        }
        // 初始化左右边界值
        leftBorder = getChildAt(0).left
        rightBorder = getChildAt(childCount - 1).right
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                preMoveX = downX
            }
            MotionEvent.ACTION_MOVE -> {
                moveX = ev.rawX
                preMoveX = moveX
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (abs(moveX - downX) > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                moveX = event.rawX
                val scrolledX = (preMoveX - moveX).toInt()
                if (scrollX + scrolledX < leftBorder - width / 2) {
                    scrollTo(leftBorder - width / 2, 0)
                    return true
                } else if (scrollX + width + scrolledX > rightBorder + width / 2) {
                    scrollTo(rightBorder - width / 2, 0)
                    return true
                }
                scrollBy(scrolledX, 0)
                preMoveX = moveX
            }
            MotionEvent.ACTION_UP -> {
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                val targetIndex = (scrollX + width / 2) / width
                val finalIndex = if (targetIndex == 3) 2 else targetIndex
                val dx = finalIndex * width - scrollX
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                mScroller.startScroll(scrollX, 0, dx, 0) // scrollBy(dx, 0);
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }
}