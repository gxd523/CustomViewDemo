package com.demo.customview.base;

import android.app.Activity;
import android.os.Bundle;

import com.demo.customview.ScreenAdapter;

import androidx.annotation.Nullable;

/**
 * Created by guoxiaodong on 2020/6/23 21:58
 */
public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdapter.setCustomDensity(this);
    }
}
