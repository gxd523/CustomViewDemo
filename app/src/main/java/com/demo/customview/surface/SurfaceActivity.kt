package com.demo.customview.surface

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.ComponentActivity
import com.demo.customview.R
import kotlinx.android.synthetic.main.activity_surface.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SurfaceActivity : ComponentActivity() {
    private var surfaceHolder: SurfaceHolder? = null
    private var paint: Paint? = null
    private var threadPool: ExecutorService? = null
    private var left = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface)
        surface_view.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.d("gxd", "MainActivity.surfaceCreated-->" + holder.hashCode())
                surfaceHolder = holder
                val canvas = holder.lockCanvas()
                onDraw(canvas)
                holder.unlockCanvasAndPost(canvas)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                Log.d("gxd", "MainActivity.surfaceChanged-->" + holder.hashCode())
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.d("gxd", "MainActivity.surfaceDestroyed-->" + holder.hashCode())
            }
        })
        paint = Paint()
        paint!!.color = Color.RED
        threadPool = Executors.newSingleThreadExecutor()
        val viewTreeObserver = View(this).viewTreeObserver
        //        viewTreeObserver.addOnGlobalLayoutListener();
    }

    private fun onDraw(canvas: Canvas) {
        Log.d("gxd", "MainActivity.onDraw-->" + canvas.saveCount)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.a)
        val dst = Rect(0, 0, surface_view.measuredWidth, surface_view.measuredHeight)
        val src = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawBitmap(bitmap, src, dst, null)
        val mPaint = Paint()
        mPaint.color = -0x99999a
        mPaint.strokeWidth = 10f
        canvas.drawLine(0f, 0f, surface_view.measuredWidth.toFloat(), surface_view.measuredHeight.toFloat(), mPaint)
        canvas.translate(160f, 160f)
        mPaint.color = Color.BLUE
        canvas.drawCircle(100f, 100f, 100f, mPaint)
        canvas.saveLayerAlpha(0f, 0f, 250f, 250f, 0x99, Canvas.ALL_SAVE_FLAG)
        mPaint.color = Color.YELLOW
        canvas.drawCircle(200f, 200f, 100f, mPaint)
        canvas.saveLayer(150f, 150f, 250f, 250f, null, Canvas.ALL_SAVE_FLAG)
        canvas.drawColor(Color.BLACK)

//        canvas.restore();
    }

    fun createCircleBitmap(): Bitmap {
        val result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result) // 画布就是Bitmap
        val paint = Paint()
        paint.color = Color.BLUE
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.dlzs)
        val src = Rect(0, 0, bitmap.width, bitmap.height)
        val dst = Rect(0, 0, 200, 200)
        canvas.drawBitmap(bitmap, src, dst, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawCircle(100f, 100f, 100f, paint)
        return result
    }

    fun onClick(view: View) {
        threadPool!!.execute {
            val canvas = surfaceHolder!!.lockCanvas()
            Log.d("gxd", "canvas = " + canvas.hashCode() + "..." + canvas.saveCount)
            canvas.restore()
            Log.d("gxd", "canvas = " + canvas.hashCode() + "..." + canvas.saveCount)
            when (view.id) {
                R.id.button_a -> {
                    paint!!.color = Color.RED
                    canvas.drawCircle(100.let { left += it; left }.toFloat(), 100f, 100f, paint!!)
                    surfaceHolder!!.unlockCanvasAndPost(canvas)
                }
                R.id.button_b -> {
                    paint!!.color = Color.YELLOW
                    canvas.drawCircle(100.let { left += it; left }.toFloat(), 300f, 100f, paint!!)
                    surfaceHolder!!.unlockCanvasAndPost(canvas)
                }
                R.id.button_c -> {
                    paint!!.color = Color.BLUE
                    paint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    canvas.drawPaint(paint!!)
                    canvas.drawCircle(100.let { left += it; left }.toFloat(), 500f, 100f, paint!!)
                    surfaceHolder!!.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}