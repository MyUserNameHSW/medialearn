package com.hsw.medialearn.modules.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * @author: HSW
 * @data: 2023/6/9
 * @desc:
 */
class BitmapSquare constructor(width: Float, height: Float) {
    // 每个顶点的坐标数
    private val COORDS_PER_VERTEX = 3

    // 每个纹理顶点的坐标数
    private val COORDS_PER_TEXTURE_VERTEX = 2

    // 顶点的坐标
    private val squareCoords: FloatArray

    // 顶点所对应的纹理坐标
    private var texVertex = floatArrayOf(
        0f, 0f,      // top left
        0f, 1f,      // bottom left
        1f, 1f,       // bottom right
        1f, 0f     // top right
    )

    init {
        val x: Float = 1f
        val y: Float = 1f
        squareCoords = floatArrayOf(
            -x, y, 0.0f,      // top left
            -x, -y, 0.0f,      // bottom left
            x, -y, 0.0f,      // bottom right
            x, y, 0.0f       // top right
        )
    }

    // 四个顶点的缓冲数组
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(squareCoords.size * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }

    // 四个顶点的绘制顺序数组
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    // 四个顶点绘制顺序数组的缓冲数组
    private val drawListBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).order(ByteOrder.nativeOrder())
            .asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }

    // 四个纹理顶点的缓冲数组
    private val texVertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(texVertex.size * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(texVertex)
                position(0)
            }

    private var vPMatrixHandle: Int = 0

    /**
     * 顶点着色器代码;
     */
    private val vertexShaderCode =
        "attribute vec4 inputTextureCoordinate;" +
                " varying vec2 textureCoordinate;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                // 把vPosition顶点经过矩阵变换后传给gl_Position
                "  gl_Position = vPosition;" +
                "textureCoordinate = inputTextureCoordinate.xy;" +
                "}"

    /**
     * 片段着色器代码
     */
    private val fragmentShaderCode = """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            void main() {
                gl_FragColor = vec4(vec3(1.0 - texture2D(inputImageTexture, textureCoordinate)), 1.0);
            }
            """

    /**
     * 着色器程序ID引用
     */
    private var mProgram: Int

    init {
        // 编译顶点着色器和片段着色器
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        // glCreateProgram函数创建一个着色器程序，并返回新创建程序对象的ID引用
        mProgram = GLES20.glCreateProgram().also {
            // 把顶点着色器添加到程序对象
            GLES20.glAttachShader(it, vertexShader)
            // 把片段着色器添加到程序对象
            GLES20.glAttachShader(it, fragmentShader)
            // 连接并创建一个可执行的OpenGL ES程序对象
            GLES20.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        // glCreateShader函数创建一个顶点着色器或者片段着色器,并返回新创建着色器的ID引用
        val shader = GLES20.glCreateShader(type)
        // 把着色器和代码关联,然后编译着色器
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private val vertexStride: Int = COORDS_PER_VERTEX * 4
    private val textVertexStride: Int = COORDS_PER_TEXTURE_VERTEX * 4

    fun draw(textureID: Int) {
        // 激活着色器程序 Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)
        // 获取顶点着色器中的vPosition变量(因为之前已经编译过着色器代码,所以可以从着色器程序中获取);用唯一ID表示
        val position = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 允许操作顶点对象position
        GLES20.glEnableVertexAttribArray(position)
        // 将顶点数据传递给position指向的vPosition变量;将顶点属性与顶点缓冲对象关联
        GLES20.glVertexAttribPointer(
            position, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
            false, vertexStride, vertexBuffer
        )
        // 激活textureID对应的纹理单元
        GLES20.glActiveTexture(textureID)
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID)
        // 获取顶点着色器中的inputTextureCoordinate变量(纹理坐标);用唯一ID表示
        val textureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate")
        // 允许操作纹理坐标inputTextureCoordinate变量
        GLES20.glEnableVertexAttribArray(textureCoordinate)
        // 将纹理坐标数据传递给inputTextureCoordinate变量
        GLES20.glVertexAttribPointer(
            textureCoordinate, COORDS_PER_TEXTURE_VERTEX, GLES20.GL_FLOAT,
            false, textVertexStride, texVertexBuffer
        )
        // 按drawListBuffer中指定的顺序绘制四边形
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLE_STRIP, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )
        // 操作完后,取消允许操作顶点对象position
        GLES20.glDisableVertexAttribArray(position)
        GLES20.glDisableVertexAttribArray(textureCoordinate)
    }
}
