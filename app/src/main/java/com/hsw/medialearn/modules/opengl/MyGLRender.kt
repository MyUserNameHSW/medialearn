package com.hsw.medialearn.modules.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author: HSW
 * @data: 2023/6/9
 * @desc:
 */
class MyGLRender: GLSurfaceView.Renderer {
    private lateinit var triangle: Triangle
    private lateinit var rectangle: Rectangle
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置背景色为黑色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
//        triangle = Triangle()
        rectangle = Rectangle()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // OpenGL使用的是标准化设备坐标;
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
//        triangle.draw()
        rectangle.draw()
    }
}