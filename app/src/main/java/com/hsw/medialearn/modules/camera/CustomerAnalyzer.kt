package com.hsw.medialearn.modules.camera

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

/**
 * @author: HSW
 * @data: 2023/6/8
 * @desc: imageAnalysis Android CameraX 摄像头数据[ImageProxy]数据分析 [https://www.jianshu.com/p/b217be3806b3]
 */
class CustomerAnalyzer(val callback: (Double) -> Unit): ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = buffer.toByteArray()
        val pixels = data.map { it.toInt() and 0xFF }
        val average = pixels.average()
        callback(average)
        pushNV21Buffer(image)
        image.close()
    }

    fun pushNV21Buffer(image: ImageProxy) {
        val nv21Bytes = image.planes[0].buffer.toByteArray()
        // TODO: 处理图像 RTMP PUSH
        Log.d("pushNV21Buffer", "pushNV21Buffer: ${nv21Bytes.size}")
    }
}