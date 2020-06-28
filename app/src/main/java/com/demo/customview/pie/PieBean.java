package com.demo.customview.pie;

import java.util.Random;

/**
 * Created by guoxiaodong on 2019-05-19 19:28
 */
public class PieBean {
    private int[] mColors = {
            0xFFCCFF00,
            0xFF6495ED,
            0xFFE32636,
            0xFF800000,
            0xFF808000,
            0xFFFF8C69,
            0xFF808080,
            0xFFE6B800,
            0xFF7CFC00
    };
    private int color = mColors[new Random().nextInt(90) % 9];
    private float percent;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
