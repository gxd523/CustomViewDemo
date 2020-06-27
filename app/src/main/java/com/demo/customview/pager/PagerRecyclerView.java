package com.demo.customview.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by guoxiaodong on 2020/6/21 11:18
 */
public class PagerRecyclerView extends RecyclerView {
    public PagerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && getLayoutManager() instanceof PagerLayoutManager) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_UP:
                    PagerLayoutManager layoutManager = (PagerLayoutManager) getLayoutManager();
                    if (layoutManager.isKeyToNextOrPrePage(event.getKeyCode())) {
                        int offset = event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1;
                        int pageIndex = layoutManager.getPageIndexByOffset() + offset;
                        layoutManager.smoothScrollToPage(pageIndex);
                    }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
