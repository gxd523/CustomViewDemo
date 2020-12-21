package com.demo.customview.surface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;

import com.demo.customview.R;
import com.demo.customview.databinding.ActivitySurfaceBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;

public class SurfaceActivity extends ComponentActivity {
    private ActivitySurfaceBinding dataBinding;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private ExecutorService threadPool;
    private int left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_surface);
        dataBinding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("gxd", "MainActivity.surfaceCreated-->" + holder.hashCode());
                surfaceHolder = holder;

                Canvas canvas = holder.lockCanvas();

                onDraw(canvas);

                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("gxd", "MainActivity.surfaceChanged-->" + holder.hashCode());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("gxd", "MainActivity.surfaceDestroyed-->" + holder.hashCode());
            }
        });
        paint = new Paint();
        paint.setColor(Color.RED);
        threadPool = Executors.newSingleThreadExecutor();

        ViewTreeObserver viewTreeObserver = new View(this).getViewTreeObserver();
//        viewTreeObserver.addOnGlobalLayoutListener();
    }

    private void onDraw(Canvas canvas) {
        Log.d("gxd", "MainActivity.onDraw-->" + canvas.getSaveCount());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a);
        Rect dst = new Rect(0, 0, dataBinding.surfaceView.getMeasuredWidth(), dataBinding.surfaceView.getMeasuredHeight());
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, src, dst, null);

        Paint mPaint = new Paint();
        mPaint.setColor(0xFF666666);
        mPaint.setStrokeWidth(10);
        canvas.drawLine(0, 0, dataBinding.surfaceView.getMeasuredWidth(), dataBinding.surfaceView.getMeasuredHeight(), mPaint);

        canvas.translate(160, 160);

        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(100, 100, 100, mPaint);

        canvas.saveLayerAlpha(0, 0, 250, 250, 0x99, Canvas.ALL_SAVE_FLAG);
        mPaint.setColor(Color.YELLOW);
        canvas.drawCircle(200, 200, 100, mPaint);

        canvas.saveLayer(150, 150, 250, 250, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawColor(Color.BLACK);

//        canvas.restore();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    Bitmap createCircleBitmap() {
        Bitmap result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);// 画布就是Bitmap
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dlzs);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect(0, 0, 200, 200);
        canvas.drawBitmap(bitmap, src, dst, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawCircle(100, 100, 100, paint);
        return result;
    }

    public void onClick(View view) {
        threadPool.execute(() -> {
            Canvas canvas = surfaceHolder.lockCanvas();
            Log.d("gxd", "canvas = " + canvas.hashCode() + "..." + canvas.getSaveCount());
            canvas.restore();
            Log.d("gxd", "canvas = " + canvas.hashCode() + "..." + canvas.getSaveCount());

            switch (view.getId()) {
                case R.id.button_a:
                    paint.setColor(Color.RED);
                    canvas.drawCircle(left += 100, 100, 100, paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    break;
                case R.id.button_b:
                    paint.setColor(Color.YELLOW);
                    canvas.drawCircle(left += 100, 300, 100, paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    break;
                case R.id.button_c:
                    paint.setColor(Color.BLUE);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    canvas.drawPaint(paint);
                    canvas.drawCircle(left += 100, 500, 100, paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    break;
            }
        });
    }
}