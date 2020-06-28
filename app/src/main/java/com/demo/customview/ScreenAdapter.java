package com.demo.customview;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

public class ScreenAdapter {
    private static final float DESIGN_DP_WIDTH = 960;
    private static float originalApplicationDensity;
    private static float originalApplicationScaleDensity;

    public static void setCustomDensity(Activity activity) {
        final DisplayMetrics applicationDisplayMetrics = activity.getApplication().getResources().getDisplayMetrics();
        if (originalApplicationDensity == 0) {
            originalApplicationDensity = applicationDisplayMetrics.density;
            originalApplicationScaleDensity = applicationDisplayMetrics.scaledDensity;
            activity.getApplication().registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        originalApplicationScaleDensity = applicationDisplayMetrics.scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }

        final float targetDensity = applicationDisplayMetrics.widthPixels / DESIGN_DP_WIDTH;
        Log.d("gxd", "density-->" + targetDensity);
        final float targetScaleDensity = targetDensity * (originalApplicationScaleDensity / originalApplicationDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);

        applicationDisplayMetrics.density = targetDensity;
        applicationDisplayMetrics.scaledDensity = targetScaleDensity;
        applicationDisplayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaleDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }
}
