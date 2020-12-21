package com.demo.customview;

import android.app.Application;
import android.os.SystemClock;

/**
 * Created by guoxiaodong on 2020/6/28 16:36
 */
public class MyApplication extends Application {
    public static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SystemClock.sleep(1000);
    }
}
