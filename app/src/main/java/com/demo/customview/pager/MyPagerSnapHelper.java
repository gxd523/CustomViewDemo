package com.demo.customview.pager;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * Created by guoxiaodong on 2020/6/21 17:22
 */
public class MyPagerSnapHelper extends SnapHelper {
    /**
     * Flying触发速度阈值
     */
    private static final int MINIMUM_FLING_VELOCITY = 50;
    private RecyclerView mRecyclerView;

    /**
     * 计算需要滚动的向量，用于页面自动回滚对齐
     *
     * @param targetView 获取需要对齐的ItemView(对于分页布局就是页面第一个)
     * @return 需要滚动的距离向量
     */
    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int position = layoutManager.getPosition(targetView);
        int[] offset = new int[2];
        if (layoutManager instanceof PagerLayoutManager) {
            PagerLayoutManager manager = (PagerLayoutManager) layoutManager;
            offset = manager.getSnapOffset(position);
        }
        return offset;
    }

    /**
     * @return 获取需要对齐的ItemView(对于分页布局就是页面第一个)
     */
    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof PagerLayoutManager) {
            PagerLayoutManager manager = (PagerLayoutManager) layoutManager;
            return manager.findSnapView();
        }
        return null;
    }

    /**
     * @param velocityX x轴滚动速率
     * @param velocityY y轴滚动速率
     * @return 获取需要对齐的ItemView(对于分页布局就是页面第一个)的position
     */
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        int snapPosition = RecyclerView.NO_POSITION;
        if (layoutManager instanceof PagerLayoutManager) {
            PagerLayoutManager manager = (PagerLayoutManager) layoutManager;
            if (manager.canScrollHorizontally()) {
                if (velocityX > MINIMUM_FLING_VELOCITY) {
                    snapPosition = manager.findNextPageFirstItem();
                } else if (velocityX < -MINIMUM_FLING_VELOCITY) {
                    snapPosition = manager.findPrePageFirstItem();
                }
            } else if (manager.canScrollVertically()) {
                if (velocityY > MINIMUM_FLING_VELOCITY) {
                    snapPosition = manager.findNextPageFirstItem();
                } else if (velocityY < -MINIMUM_FLING_VELOCITY) {
                    snapPosition = manager.findPrePageFirstItem();
                }
            }
        }
        return snapPosition;
    }

    /**
     * 通过自定义LinearSmoothScroller来控制速度
     */
    @Nullable
    @Override
    protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new PagerSmoothScroller(mRecyclerView);
    }

    /**
     * 照搬父类实现，只是修改了Flying触发阈值
     *
     * @param velocityX x轴滚动速率
     * @param velocityY y轴滚动速率
     * @return 是否消费该事件
     */
    @Override
    public boolean onFling(int velocityX, int velocityY) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }
        int minFlingVelocity = MINIMUM_FLING_VELOCITY;// 改成我们设定的速率阈值
        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
                && snapFromFling(layoutManager, velocityX, velocityY);
    }

    /**
     * 为了使用此方法，复制了父类的该私有方法
     *
     * @param velocityX x轴滚动速率
     * @param velocityY y轴滚动速率
     * @return 是否消费该事件
     */
    private boolean snapFromFling(@NonNull RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return false;
        }

        RecyclerView.SmoothScroller smoothScroller = createScroller(layoutManager);
        if (smoothScroller == null) {
            return false;
        }

        int targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY);
        if (targetPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        smoothScroller.setTargetPosition(targetPosition);
        layoutManager.startSmoothScroll(smoothScroller);
        return true;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }
}
