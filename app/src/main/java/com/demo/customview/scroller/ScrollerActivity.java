package com.demo.customview.scroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.demo.customview.R;

import androidx.annotation.Nullable;

/**
 * Created by guoxiaodong on 12/21/20 17:09
 */
public class ScrollerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller);

        Button aBtn = findViewById(R.id.activity_scroller_a_btn);
        aBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("gxd", "ScrollerActivity.onClick-->");
            }
        });
    }
}