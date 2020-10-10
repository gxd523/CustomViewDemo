package com.demo.customview.snaphelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.demo.customview.base.BaseActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SnapHelperActivity extends BaseActivity {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SnapHelperActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = new RecyclerView(this);
        setContentView(recyclerView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        recyclerView.setLayoutParams(layoutParams);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new SnapHelperAdapter());

        new GallerySnapHelper().attachToRecyclerView(recyclerView);
    }
}
