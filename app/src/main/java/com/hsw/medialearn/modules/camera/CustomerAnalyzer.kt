package com.hsw.medialearn.modules.camera

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
        image.close()
    }


}