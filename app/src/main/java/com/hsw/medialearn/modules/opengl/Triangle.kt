package com.hsw.medialearn.modules.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author: HSW
 * @data: 2023/6/9
 * @desc:
 */
class Triangle {
    // 三角形三个点的坐标值(逆时针方向,在3D坐标系中,方向决定了哪面是正面)
    private var triangleCoords = floatArrayOf(
        0.0f, 0.5f, 0.0f,      // top
        -0.5f, -0.5f, 0.0f,    // bottom left
        0.8f, -0.5f, 0.0f      // bottom right
    )
    // 每个顶点的坐标数
    val COORDS_PER_VERTEX = 3

    // 设置颜色(分别代表red, green, blue and alpha)
    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        // 坐标点的数目 * float所占字节
        ByteBuffer.allocateDirect(triangleCoords.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                // 把坐标添加到FloatBuffer
                put(triangleCoords)
                // 设置buffer的位置为起始点0
                position(0)
            }

    /**
     * 顶点着色器代码;
     */
    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    /**
     * 片段着色器代码
     */
    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

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

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    /**
     * 实际绘制时执行的方法
     **/
    fun draw() {
        // 激活着色器程序,把程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram)
        // 获取顶点着色器中的vPosition变量(因为之前已经编译过着色器代码,所以可以从着色器程序中获取);用唯一ID表示
        val position = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 允许操作顶点对象position
        GLES20.glEnableVertexAttribArray(position)
        // 将顶点数据传递给position指向的vPosition变量;将顶点属性与顶点缓冲对象关联
        GLES20.glVertexAttribPointer(
            position, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
            false, vertexStride, vertexBuffer)
        // 获取片段着色器中的vColor变量
        val colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        // 通过colorHandle设置绘制的颜色值
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        // 绘制顶点数组;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        // 操作完后,取消允许操作顶点对象position
        GLES20.glDisableVertexAttribArray(position)
    }
}
