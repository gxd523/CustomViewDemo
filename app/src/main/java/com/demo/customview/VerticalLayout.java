package com.demo.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by guoxiaodong on 2020/6/12 12:52
 */
public class VerticalLayout extends ViewGroup {
    public VerticalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 将所有的子View进行测量，这会触发每个子View的onMeasure函数
        // 注意要与measureChild区分，measureChild是对单个view进行测量
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();

        if (childCount == 0) {// 如果没有子View,当前ViewGroup没有存在的意义，不用占用空间
            setMeasuredDimension(0, 0);
        } else {// 如果宽高都是包裹内容
            if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {// 我们将高度设置为所有子View的高度相加，宽度设为子View中最大的宽度
                int height = getTotalHeight();
                int width = getMaxChildWidth();
                Log.d("gxd", "VerticalLayout.onMeasure-->" + width + "..." + height);
                setMeasuredDimension(width, height);
            } else if (heightMode == MeasureSpec.AT_MOST) {// 如果只有高度是包裹内容
                // 宽度设置为ViewGroup自己的测量宽度，高度设置为所有子View的高度总和
                setMeasuredDimension(widthSize, getTotalHeight());
                Log.d("gxd", "VerticalLayout.onMeasure-->");
            } else if (widthMode == MeasureSpec.AT_MOST) {// 如果只有宽度是包裹内容
                // 宽度设置为子View中宽度最大的值，高度设置为ViewGroup自己的测量值
                setMeasuredDimension(getMaxChildWidth(), heightSize);
                Log.d("gxd", "VerticalLayout.onMeasure-->");
            }
        }
    }

    /**
     * 注意：这里的4个入参是当前view相对于父view的相对位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("gxd", "VerticalLayout.onLayout-->" + t);
        int curHeight = 0;// 记录当前的高度位置
        for (int i = 0; i < getChildCount(); i++) {// 将子View逐个摆放
            View child = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            // 摆放子View，参数分别是子View矩形区域的左、上、右、下边
            child.layout(
                    l + params.leftMargin,
                    params.topMargin + curHeight,
                    l + params.leftMargin + width,
                    curHeight + params.topMargin + height
            );
            curHeight += params.topMargin + height + params.bottomMargin;
        }
    }

    /**
     * 获取子View中宽度最大的值
     */
    private int getMaxChildWidth() {
        int childCount = getChildCount();
        int maxWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            int childWidthWithMargin = params.leftMargin + childView.getMeasuredWidth() + params.rightMargin;
            if (childWidthWithMargin > maxWidth) {
                maxWidth = childWidthWithMargin;
            }
        }
        return maxWidth;
    }

    /**
     * 将所有子View的高度相加
     */
    private int getTotalHeight() {
        int childCount = getChildCount();
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            height += params.topMargin + childView.getMeasuredHeight() + params.bottomMargin;

        }
        return height;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new ViewGroup.MarginLayoutParams(lp);
    }
}
