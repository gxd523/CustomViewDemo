package com.demo.customview.recycler;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewWrapper extends RecyclerView {
    private LayoutListener layoutListener;

    public RecyclerViewWrapper(@NonNull Context context) {
        super(context);
    }

    public void setLayoutListener(LayoutListener layoutListener) {
        this.layoutListener = layoutListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (layoutListener != null) {
            layoutListener.onBeforeLayout();
        }
        super.onLayout(changed, l, t, r, b);

        if (layoutListener != null) {
            layoutListener.onAfterLayout();
        }
    }

    public interface LayoutListener {
        void onBeforeLayout();

        void onAfterLayout();
    }
}
