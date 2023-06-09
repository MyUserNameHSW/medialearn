package com.hsw.medialearn.modules.canvas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.hsw.medialearn.R

/**
 * @author: HSW
 * @data: 2023/6/8
 * @desc: 使用SurfaceView绘制一张图片
 */
class CustomSurfaceImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): SurfaceView(context, attrs, defStyleAttr),
    SurfaceHolder.Callback {
    private val bitmap: Bitmap
    private val paint = Paint()
    init {
        bitmap = ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!.toBitmap()
        holder.addCallback(this)
        paint.isFilterBitmap = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(bitmap.width, bitmap.height)
    }

    private fun drawImage() {
        val canvas = holder.lockCanvas()
        canvas?.let {
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawImage()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        drawImage()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        bitmap.recycle()
    }
}