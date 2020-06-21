package com.demo.customview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.demo.customview.pager.PagerActivity;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdapter.setCustomDensity(this);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, PagerActivity.class);
        startActivity(intent);
    }
}