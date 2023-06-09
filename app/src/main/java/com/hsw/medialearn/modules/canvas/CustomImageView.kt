package com.hsw.medialearn.modules.canvas

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.hsw.medialearn.R

/**
 * @author: HSW
 * @data: 2023/6/8
 * @desc: 使用ImageView绘制一张图片
 */
class CustomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): AppCompatImageView(context, attrs, defStyleAttr) {
    init {
        setImageResource(R.mipmap.ic_launcher_round)
    }
}