package com.demo.customview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.demo.customview.base.BaseActivity;
import com.demo.customview.databinding.ActivityMainBinding;
import com.demo.customview.pager.PagerActivity;
import com.demo.customview.pie.PieBean;
import com.demo.customview.snaphelper.SnapHelperActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        dataBinding.pieView.setStartAngle(0);
        List<PieBean> pieBeanList = new ArrayList<PieBean>() {{
            PieBean pieBean;

            pieBean = new PieBean();
            pieBean.setPercent(.1f);
            add(pieBean);

            pieBean = new PieBean();
            pieBean.setPercent(.2f);
            add(pieBean);

            pieBean = new PieBean();
            pieBean.setPercent(.3f);
            add(pieBean);

            pieBean = new PieBean();
            pieBean.setPercent(.4f);
            add(pieBean);
        }};

        dataBinding.pieView.setPieList(pieBeanList);

        dataBinding.textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onClick(View view) {
        Class<? extends Activity> activityClass;
        switch (view.getId()) {
            case R.id.pie_view:
                activityClass = PagerActivity.class;
                break;
            case R.id.my_view:
                activityClass = SnapHelperActivity.class;
                break;
            default:
                activityClass = MainActivity.class;
        }
        startActivity(new Intent(MainActivity.this, activityClass));
    }
}