package com.demo.customview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class MyShadowView extends View {
    private final int radius = 50;
    private final Paint paint;
    private int width;
    private int height;
    private Rect rect;

    public MyShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);// 关闭硬件加速

        paint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (width == 0) {
            width = w;
        }
        if (height == 0) {
            height = h;
        }
        rect = new Rect(radius, radius, width - radius, height - radius);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap srcBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        super.draw(new Canvas(srcBitmap));

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap dstBitmap = getShadowBitmap();
        canvas.drawBitmap(
                dstBitmap,
                null,
                new Rect(0, 0, dstBitmap.getWidth(), dstBitmap.getHeight()),
                paint
        );

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(
                srcBitmap,
                null,
                new Rect(radius, radius, srcBitmap.getWidth() - radius, srcBitmap.getHeight() - radius),
                paint
        );
    }

    Bitmap getShadowBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setShadowLayer(radius, 20, 20, 0xFF000000);
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        canvas.drawRect(rect, paint);
        return bitmap;
    }
}