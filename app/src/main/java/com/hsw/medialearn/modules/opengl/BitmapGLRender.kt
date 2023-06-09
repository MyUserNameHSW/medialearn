package com.hsw.medialearn.modules.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author: HSW
 * @data: 2023/6/9
 * @desc:
 */
class BitmapGLRender : GLSurfaceView.Renderer {

    private lateinit var bitmapSquare: BitmapSquare

    // 纹理ID
    private var glTextureId = 0
    private var bitmap: Bitmap? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        bitmapSquare = BitmapSquare(bitmap?.width?.toFloat()?:0f, bitmap?.height?.toFloat()?:0f)
        // 创建纹理
        glTextureId = OpenGLUtils.createTexture(
            bitmap, GLES20.GL_NEAREST, GLES20.GL_NEAREST,
            GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE
        )
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        bitmapSquare.draw(glTextureId)
    }

    fun setImageBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
    }
}
