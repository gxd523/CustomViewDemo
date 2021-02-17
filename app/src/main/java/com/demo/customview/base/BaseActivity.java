package com.demo.customview.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.demo.customview.ScreenAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by guoxiaodong on 2020/6/23 21:58
 */
public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("gxd", getClass().getSimpleName() + ".onCreate-->");
        super.onCreate(savedInstanceState);
        ScreenAdapter.setCustomDensity(this);
    }

    @Override
    protected void onStart() {
        Log.d("gxd", getClass().getSimpleName() + ".onStart-->");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d("gxd", getClass().getSimpleName() + ".onRestart-->");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("gxd", getClass().getSimpleName() + ".onResume-->");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("gxd", getClass().getSimpleName() + ".onPause-->");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("gxd", getClass().getSimpleName() + ".onStop-->");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("gxd", getClass().getSimpleName() + ".onDestroy-->");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("gxd", "BaseActivity.onSaveInstanceState-->");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d("gxd", "BaseActivity.onRestoreInstanceState-->");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
