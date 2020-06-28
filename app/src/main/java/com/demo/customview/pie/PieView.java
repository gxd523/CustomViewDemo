package com.demo.customview.pie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by guoxiaodong on 2019-05-19 19:25
 */
public class PieView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private float mStartAngle;
    private List<PieBean> pieBeanList;
    private RectF rectF;

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        float r = Math.min(mWidth, mHeight) >> 1;
        rectF = new RectF(-r, -r, r, r);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth >> 1, mHeight >> 1);

        float currentAngle = mStartAngle;
        for (PieBean pieBean : pieBeanList) {
            mPaint.setColor(pieBean.getColor());
            float tempAngle = pieBean.getPercent() * 360;
            canvas.drawArc(rectF, currentAngle, tempAngle, true, mPaint);
            currentAngle += tempAngle;
        }
    }

    public void setStartAngle(float startAngle) {
        this.mStartAngle = startAngle;
    }

    public void setPieList(List<PieBean> pieBeanList) {
        this.pieBeanList = pieBeanList;
        invalidate();
    }
}
