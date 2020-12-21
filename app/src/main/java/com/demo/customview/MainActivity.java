package com.demo.customview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.demo.customview.scroller.ScrollerActivity;

import androidx.annotation.Nullable;

/**
 * Created by guoxiaodong on 12/21/20 17:14
 */
public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.activity_main_scroller_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.activity_main_scroller_btn:
                intent.setClass(this, ScrollerActivity.class);
                break;
        }

        startActivity(intent);
    }
}