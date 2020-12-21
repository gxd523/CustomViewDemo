package com.demo.customview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.demo.customview.scroller.ScrollerActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by guoxiaodong on 12/21/20 17:14
 */
class MainActivity : Activity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main_scroller_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_main_scroller_btn -> Intent(this, ScrollerActivity::class.java)
            else -> null
        }?.let {
            startActivity(it)
        }
    }
}