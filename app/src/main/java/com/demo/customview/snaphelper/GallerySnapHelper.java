package com.demo.customview.snaphelper;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * Created by guoxiaodong on 2020/6/23 21:48
 */
public class GallerySnapHelper extends SnapHelper {
    private static final float INVALID_DISTANCE = 1f;
    /**
     * 滚动速度
     */
    private static final float MILLISECONDS_PER_INCH = 4f;
    private OrientationHelper mHorizontalHelper;
    private RecyclerView mRecyclerView;

    /**
     * 寻找最接近对齐位置的ItemView
     */
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        OrientationHelper horizontalHelper = getHorizontalHelper(layoutManager);
        if (layoutManager instanceof LinearLayoutManager) {
            int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            if (firstChildPosition == RecyclerView.NO_POSITION) {
                return null;
            }

            if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                return null;
            }

            View firstChildView = layoutManager.findViewByPosition(firstChildPosition);
            int firstChildEnd = horizontalHelper.getDecoratedEnd(firstChildView);
            if (firstChildEnd >= horizontalHelper.getDecoratedMeasurement(firstChildView) / 2 && firstChildEnd > 0) {
                return firstChildView;
            } else {
                return layoutManager.findViewByPosition(firstChildPosition + 1);
            }
        } else {
            return null;
        }
    }

    /**
     * 最接近对齐位置的ItemView和对齐位置的距离
     */
    @Override
    @NonNull
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            OrientationHelper horizontalHelper = getHorizontalHelper(layoutManager);
            out[0] = horizontalHelper.getDecoratedStart(targetView) - horizontalHelper.getStartAfterPadding();
        }
        return out;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final View snapView = findSnapView(layoutManager);
        if (snapView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int snapPosition = layoutManager.getPosition(snapView);
        if (snapPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider = (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
        // deltaJumps sign comes from the velocity which may not match the order of children in
        // the LayoutManager. To overcome this, we ask for a vector from the LayoutManager to
        // get the direction.
        PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            // cannot get a vector for the given position.
            return RecyclerView.NO_POSITION;
        }

        OrientationHelper horizontalHelper = getHorizontalHelper(layoutManager);

        // 在松手之后,列表最多只能滚多一屏的item数
        int deltaThreshold = layoutManager.getWidth() / horizontalHelper.getDecoratedMeasurement(snapView);

        int hDeltaJump;
        if (layoutManager.canScrollHorizontally()) {
            int[] distances = calculateScrollDistance(velocityX, 0);
            float distancePerChild = computeDistancePerChild(layoutManager, horizontalHelper);
            int distance = distances[0];
            if (distance > 0) {
                hDeltaJump = (int) Math.floor(distance / distancePerChild);// 不大于的最大整数
            } else {
                hDeltaJump = (int) Math.ceil(distance / distancePerChild);// 不小于的最小整数
            }

            if (hDeltaJump > deltaThreshold) {
                hDeltaJump = deltaThreshold;
            }
            if (hDeltaJump < -deltaThreshold) {
                hDeltaJump = -deltaThreshold;
            }

            if (vectorForEnd.x < 0) {
                hDeltaJump = -hDeltaJump;
            }
        } else {
            hDeltaJump = 0;
        }

        if (hDeltaJump == 0) {
            return RecyclerView.NO_POSITION;
        }

        int targetPos = snapPosition + hDeltaJump;
        if (targetPos < 0) {
            targetPos = 0;
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1;
        }
        return targetPos;
    }

    @Nullable
    @Override
    protected RecyclerView.SmoothScroller createScroller(final RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                int[] snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        mRecyclerView = recyclerView;
        super.attachToRecyclerView(recyclerView);
    }

    /**
     * 就是算ItemView宽度(包含margin)，可能是兼容了ItemView尺寸不同的情况
     */
    private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
        View minPosView = null;
        View maxPosView = null;
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return INVALID_DISTANCE;
        }

        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            final int pos = layoutManager.getPosition(child);
            if (pos == RecyclerView.NO_POSITION) {
                continue;
            }
            if (pos < minPos) {
                minPos = pos;
                minPosView = child;
            }
            if (pos > maxPos) {
                maxPos = pos;
                maxPosView = child;
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE;
        }
        int start = Math.min(helper.getDecoratedStart(minPosView), helper.getDecoratedStart(maxPosView));
        int end = Math.max(helper.getDecoratedEnd(minPosView), helper.getDecoratedEnd(maxPosView));
        int distance = end - start;
        if (distance == 0) {
            return INVALID_DISTANCE;
        }
        return 1f * distance / ((maxPos - minPos) + 1);
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        return super.onFling(velocityX, velocityY);
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}
