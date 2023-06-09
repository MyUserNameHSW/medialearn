package com.hsw.medialearn.modules.canvas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.hsw.medialearn.R

/**
 * @author: HSW
 * @data: 2023/6/8
 * @desc: 使用自定义View绘制一张图片
 */
class MyImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {
    private val bitmap: Bitmap
    private val paint = Paint()
    init {
        bitmap = ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!.toBitmap()
        paint.isFilterBitmap = true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(bitmap.width, bitmap.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }
}