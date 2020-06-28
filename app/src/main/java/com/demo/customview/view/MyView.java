package com.demo.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.demo.customview.R;

/**
 * Created by guoxiaodong on 2020/6/12 10:50
 */
public class MyView extends View {
    public static final int DEFAULT_SIZE_DP = 100;
    private int width;
    private int height;
    private Paint paint;
    private int defaultSize;
    private int mRadius;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        defaultSize = typedArray.getDimensionPixelSize(R.styleable.MyView_default_size, (int) (DEFAULT_SIZE_DP * displayMetrics.density));
        mRadius = typedArray.getDimensionPixelSize(R.styleable.MyView_radius, (int) (DEFAULT_SIZE_DP / 2 * displayMetrics.density));
        typedArray.recycle();

        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(widthMeasureSpec);
        int height = getSize(heightMeasureSpec);

        if (width < height) {
            height = width;
        } else {
            width = height;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = mRadius == 0 ? Math.min(width, height) / 2 : mRadius;
        paint.setColor(0xFFFF9900);
        int centerX = width / 2;
        int centerY = height / 2;
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private int getSize(int measureSpec) {
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:// 如果测量模式是最大取值为size
            case MeasureSpec.EXACTLY:// 如果是固定的大小，那就不要去改变它
                return MeasureSpec.getSize(measureSpec);
            case MeasureSpec.UNSPECIFIED:// 如果没有指定大小，就设置为默认大小
            default:
                return defaultSize;
        }
    }
}
