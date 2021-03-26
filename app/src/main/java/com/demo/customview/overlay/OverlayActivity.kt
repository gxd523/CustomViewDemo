package com.demo.customview.overlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.demo.customview.R
import kotlinx.android.synthetic.main.activity_overlay.*
import kotlin.math.min

/**
 * Created by guoxiaodong on 3/25/21 20:49
 */
class OverlayActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_overlay)
        overlayBtn.setOnClickListener {
            val contentLayout: ViewGroup = findViewById(android.R.id.content)
            contentLayout.overlay.add(overlayBtn)// View添加到Overlay不会响应触摸事件

            ObjectAnimator.ofPropertyValuesHolder(
                overlayBtn,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y.name, 0f, resources.displayMetrics.heightPixels.toFloat(), 0f),
                PropertyValuesHolder.ofFloat(View.ROTATION.name, 0f, 360f, 0f)
            ).apply {
                duration = 2000
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        contentLayout.overlay.remove(overlayBtn)
                        redLayout.addView(overlayBtn)
                    }
                })
            }.start()
        }

        orangeLayout.post {
            orangeLayout.overlay.add(OverlayDrawable().apply {
                val padding = min(orangeLayout.width, orangeLayout.height) / 4
                setBounds(padding, padding, orangeLayout.width - padding, orangeLayout.height - padding)
            })
        }
    }
}