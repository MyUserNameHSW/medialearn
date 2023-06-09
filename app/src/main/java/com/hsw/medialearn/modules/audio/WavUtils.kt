package com.hsw.medialearn.modules.audio

import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

/**
 * @author: HSW
 * @data: 2023/6/8
 * @desc:
 */
object WavUtils {

    @Throws(IOException::class)
    fun writeHeader(fileOutputStream: FileOutputStream, bufferSizeInBytes: Int) {
        fileOutputStream.write(
            byteArrayOf(
                'R'.code.toByte(),
                'I'.code.toByte(),
                'F'.code.toByte(),
                'F'.code.toByte()
            )
        )
        fileOutputStream.write(intToByteArray(36 + bufferSizeInBytes))
        fileOutputStream.write(
            byteArrayOf(
                'W'.code.toByte(),
                'A'.code.toByte(),
                'V'.code.toByte(),
                'E'.code.toByte()
            )
        )
        fileOutputStream.write(
            byteArrayOf(
                'f'.code.toByte(),
                'm'.code.toByte(),
                't'.code.toByte(),
                ' '.code.toByte()
            )
        )
        fileOutputStream.write(intToByteArray(16))
        fileOutputStream.write(shortToByteArray(1.toShort()))
        fileOutputStream.write(shortToByteArray(1.toShort()))
        fileOutputStream.write(intToByteArray(44100))
        fileOutputStream.write(intToByteArray(44100 * 16 / 8))
        fileOutputStream.write(shortToByteArray((1 * 16 / 8).toShort()))
        fileOutputStream.write(shortToByteArray(16.toShort()))
        fileOutputStream.write(
            byteArrayOf(
                'd'.code.toByte(),
                'a'.code.toByte(),
                't'.code.toByte(),
                'a'.code.toByte()
            )
        )
        fileOutputStream.write(intToByteArray(bufferSizeInBytes))
    }

    @Throws(IOException::class)
    fun writeDataLength(fileChannel: FileChannel, fileSize: Long) {
        fileChannel.position(4)
        fileChannel.write(intToByteBuffer((36 + fileSize).toInt()))
        fileChannel.position(40)
        fileChannel.write(intToByteBuffer(fileSize.toInt()))
    }

    fun byteArrayToInt(buffer: ByteArray): Int {
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt()
    }

    fun byteArrayToShort(buffer: ByteArray): Short {
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort()
    }

    private fun intToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array()
    }

    private fun intToByteBuffer(value: Int): ByteBuffer {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value)
    }

    private fun shortToByteBuffer(value: Short): ByteBuffer {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value)
    }
}