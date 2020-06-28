package com.demo.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ShadowView extends View {
    private Paint paint;
    private RectF rectF;
    private int radius;

    public ShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);// 关闭硬件加速
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0x00000000);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint.setShadowLayer(20, 4, 3, 0x33121212);
        radius = Math.min(w, h) / 2;
        rectF = new RectF(25, 25, w - 25, h - 25);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rectF, radius, radius, paint);
    }
}