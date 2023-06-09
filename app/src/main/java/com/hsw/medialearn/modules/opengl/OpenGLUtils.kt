package com.hsw.medialearn.modules.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils


/**
 * @author: HSW
 * @data: 2023/6/9
 * @desc:
 */
object OpenGLUtils {
    /**
     * 根据bitmap创建2D纹理
     * @param bitmap
     * @param minFilter     缩小过滤类型 (1.GL_NEAREST ; 2.GL_LINEAR)
     * @param magFilter     放大过滤类型
     * @param wrapS         纹理S方向边缘环绕;也称作X方向
     * @param wrapT         纹理T方向边缘环绕;也称作Y方向
     * @return 返回创建的 Texture ID
     */
    fun createTexture(
        bitmap: Bitmap?,
        minFilter: Int,
        magFilter: Int,
        wrapS: Int,
        wrapT: Int
    ): Int {
        val textureHandle =
            createTextures(GLES20.GL_TEXTURE_2D, 1, minFilter, magFilter, wrapS, wrapT)
        if (bitmap != null) {
            // 4.把bitmap加载到纹理中
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        }
        return textureHandle[0]
    }

    /**
     * 创建纹理
     * @param textureTarget Texture类型。
     * 1. 相机用 GLES11Ext.GL_TEXTURE_EXTERNAL_OES
     * 2. 图片用 GLES20.GL_TEXTURE_2D
     * @param count         创建纹理数量
     * @param minFilter     缩小过滤类型 (1.GL_NEAREST ; 2.GL_LINEAR)
     * @param magFilter     放大过滤类型
     * @param wrapS         纹理S方向边缘环绕;也称作X方向
     * @param wrapT         纹理T方向边缘环绕;也称作Y方向
     * @return 返回创建的 Texture ID
     */
    fun createTextures(
        textureTarget: Int, count: Int, minFilter: Int, magFilter: Int, wrapS: Int,
        wrapT: Int
    ): IntArray {
        val textureHandles = IntArray(count)
        for (i in 0 until count) {
            // 1.生成纹理
            GLES20.glGenTextures(1, textureHandles, i)
            // 2.绑定纹理
            GLES20.glBindTexture(textureTarget, textureHandles[i])
            // 3.设置纹理属性
            // 设置纹理的缩小过滤类型（1.GL_NEAREST ; 2.GL_LINEAR）
            GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter.toFloat())
            // 设置纹理的放大过滤类型（1.GL_NEAREST ; 2.GL_LINEAR）
            GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter.toFloat())
            // 设置纹理的X方向边缘环绕
            GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS)
            // 设置纹理的Y方向边缘环绕
            GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT)
        }
        return textureHandles
    }
}
