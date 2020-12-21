package com.demo.customview.scroller

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.demo.customview.R
import kotlinx.android.synthetic.main.activity_scroller.*

class ScrollerActivity : Activity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroller)

        activity_scroller_a_btn.setOnClickListener(this)
        activity_scroller_b_btn.setOnClickListener(this)
        activity_scroller_c_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v !is TextView) {
            return
        }
        Log.d("gxd", "ScrollerActivity.onClick-->${v.text}")
    }
}