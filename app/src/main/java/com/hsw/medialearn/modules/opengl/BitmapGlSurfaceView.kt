package com.hsw.medialearn.modules.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.hsw.medialearn.R

/**
 * @author: HSW
 * @data: 2023/6/9
 * @desc:
 */
class BitmapGlSurfaceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    private lateinit var renderer: BitmapGLRender

    init {
            setEGLContextClientVersion(2)
            renderer = BitmapGLRender()
            setRenderer(renderer)
            renderMode = RENDERMODE_WHEN_DIRTY
            val bitmap = ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!.toBitmap()
            renderer.setImageBitmap(bitmap)
    }
}