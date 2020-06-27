package com.demo.customview.pager;

import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by guoxiaodong on 2020/6/27 11:21
 */
public class PagerSmoothScroller extends LinearSmoothScroller {
    private static final float MILLISECONDS_PER_INCH = 25f;
    private RecyclerView mRecyclerView;

    public PagerSmoothScroller(RecyclerView recyclerView) {
        super(recyclerView.getContext());
        mRecyclerView = recyclerView;
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
        if (manager instanceof PagerLayoutManager) {
            PagerLayoutManager layoutManager = (PagerLayoutManager) manager;
            int snapPosition = mRecyclerView.getChildAdapterPosition(targetView);
            int[] snapDistances = layoutManager.getSnapOffset(snapPosition);
            final int dx = snapDistances[0];
            final int dy = snapDistances[1];
            final int time = calculateTimeForScrolling(Math.max(Math.abs(dx), Math.abs(dy)));
            if (time > 0) {
                action.update(dx, dy, time, mDecelerateInterpolator);
            }
        }
    }

    @Override
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
    }

    /**
     * 控制滚动速度
     */
    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }
}
