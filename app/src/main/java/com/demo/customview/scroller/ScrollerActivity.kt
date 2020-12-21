package com.demo.customview.scroller

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.demo.customview.R
import kotlinx.android.synthetic.main.activity_scroller.*

class ScrollerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroller)
        activity_scroller_a_btn.setOnClickListener { Log.d("gxd", "ScrollerActivity.onClick-->${activity_scroller_a_btn.text}") }
    }
}