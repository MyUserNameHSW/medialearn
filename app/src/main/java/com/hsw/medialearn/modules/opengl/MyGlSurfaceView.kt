package com.hsw.medialearn.modules.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * @author: HSW
 * @data: 2023/6/9
 * @desc:
 */
class MyGlSurfaceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null): GLSurfaceView(context, attrs) {
    private val renderer: MyGLRender
    init {
        setEGLContextClientVersion(2)
        renderer = MyGLRender()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}