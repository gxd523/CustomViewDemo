package com.demo.customview.recycler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerCacheActivity extends Activity {
    private RecyclerViewWrapper mRecyclerView;
    private String mTraversalAttachList;
    private String mTraversalCacheList;
    private String mTraversalRecyclerPool;
    private int mViewCacheMax;
    private int recyclerPoolMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerViewWrapper(this);
//        mRecyclerView.setItemViewCacheSize(10);
        setContentView(mRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(new RecyclerCacheAdapter());

        mRecyclerView.setLayoutListener(new RecyclerViewWrapper.LayoutListener() {
            @Override
            public void onBeforeLayout() {
                reflectRecyclerCacheList();
            }

            @Override
            public void onAfterLayout() {
                reflectRecyclerCacheList();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                reflectRecyclerCacheList();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                reflectRecyclerCacheList();
            }
        });
    }

    private void reflectRecyclerCacheList() {
        try {
            Field mRecyclerField = RecyclerView.class.getDeclaredField("mRecycler");
            mRecyclerField.setAccessible(true);
            RecyclerView.Recycler mRecycler = (RecyclerView.Recycler) mRecyclerField.get(mRecyclerView);

            // 屏幕内的ViewHolder集合
            Field mAttachedScrapField = RecyclerView.Recycler.class.getDeclaredField("mAttachedScrap");
            mAttachedScrapField.setAccessible(true);
            ArrayList<RecyclerView.ViewHolder> mAttachedScrap = (ArrayList<RecyclerView.ViewHolder>) mAttachedScrapField.get(mRecycler);
            if (mAttachedScrap.size() > 0) {
                String traversalAttachList = traversalList("AttachList", mAttachedScrap);
                if (!traversalAttachList.equals(mTraversalAttachList)) {
                    mTraversalAttachList = traversalAttachList;
                    Log.d("gxd", mTraversalAttachList);
                }
            }

            // 刚移出屏幕的ViewHolder集合
            Field mCachedViewsField = RecyclerView.Recycler.class.getDeclaredField("mCachedViews");
            mCachedViewsField.setAccessible(true);
            ArrayList<RecyclerView.ViewHolder> mCachedViews = (ArrayList<RecyclerView.ViewHolder>) mCachedViewsField.get(mRecycler);
            if (mCachedViews.size() > 0) {
                String traversalCacheList = traversalList("CacheList", mCachedViews);
                if (!traversalCacheList.equals(mTraversalCacheList)) {
                    mTraversalCacheList = traversalCacheList;
                    Log.d("gxd", mTraversalCacheList);
                }
            }

            // 刚移出屏幕的ViewHolder集合的大小限制
            Field mViewCacheMaxField = RecyclerView.Recycler.class.getDeclaredField("mViewCacheMax");
            mViewCacheMaxField.setAccessible(true);
            int viewCacheMax = (int) mViewCacheMaxField.get(mRecycler);
            if (mViewCacheMax != viewCacheMax) {
                mViewCacheMax = viewCacheMax;
                Log.d("gxd", "max cache = " + mViewCacheMax);
            }


            Field mRecyclerPoolField = RecyclerView.Recycler.class.getDeclaredField("mRecyclerPool");
            mRecyclerPoolField.setAccessible(true);
            RecyclerView.RecycledViewPool mRecyclerPool = (RecyclerView.RecycledViewPool) mRecyclerPoolField.get(mRecycler);
            Field mScrapField = RecyclerView.RecycledViewPool.class.getDeclaredField("mScrap");
            mScrapField.setAccessible(true);
            SparseArray mScrap = (SparseArray) mScrapField.get(mRecyclerPool);

            if (mScrap.size() > 1) {
                Log.d("gxd", "mScrap.size() > 1 !!!!!!!!!!!!!!!!!!!!!!!!!");
            } else if (mScrap.size() == 1) {
                Class<?> ScrapDataClass = Class.forName(String.format("%s$ScrapData", RecyclerView.RecycledViewPool.class.getName()));
                Field mScrapHeapField = ScrapDataClass.getDeclaredField("mScrapHeap");
                mScrapHeapField.setAccessible(true);
                Object scrapData = mScrap.get(0);
                ArrayList<RecyclerView.ViewHolder> mScrapHeap = (ArrayList<RecyclerView.ViewHolder>) mScrapHeapField.get(scrapData);
                if (mScrapHeap.size() > 0) {
                    String traversalRecyclerPool = traversalList("RecyclerPool", mScrapHeap);
                    if (!traversalRecyclerPool.equals(mTraversalRecyclerPool)) {
                        mTraversalRecyclerPool = traversalRecyclerPool;
                        Log.d("gxd", mTraversalRecyclerPool);
                    }
                }

                Field mMaxScrapField = ScrapDataClass.getDeclaredField("mMaxScrap");
                mMaxScrapField.setAccessible(true);
                int maxScrap = (int) mMaxScrapField.get(scrapData);
                if (recyclerPoolMax != maxScrap) {
                    recyclerPoolMax = maxScrap;
                    Log.d("gxd", "max recycler pool = " + recyclerPoolMax);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String traversalList(String name, List<?> list) {
        StringBuilder stringBuilder = new StringBuilder(name + "...");
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            stringBuilder.append(RecyclerCacheAdapter.viewHolderHashCodeList.indexOf(o.hashCode()) + 1);
            if (i + 1 != list.size()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
