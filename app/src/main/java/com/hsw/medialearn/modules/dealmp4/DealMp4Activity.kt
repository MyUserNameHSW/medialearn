package com.hsw.medialearn.modules.dealmp4

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.hsw.medialearn.R
import com.hsw.medialearn.databinding.ActivityDealMp4Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer

class DealMp4Activity : AppCompatActivity() {

    private lateinit var videoFilePath: String
    private lateinit var outFilePath: String
    private var extractor: MediaExtractor? = null
    private var muxer: MediaMuxer? = null

    private lateinit var binding: ActivityDealMp4Binding

    private var videoTrackIndex = -1
    private var audioTrackIndex = -1

    private var videoHeight = 0
    private var videoWidth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDealMp4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        videoFilePath = "$filesDir/private.mp4"
        val outFileDir = File("$filesDir/output/")
        if (!outFileDir.exists()) {
            outFileDir.mkdir()
        }
        outFilePath = "$filesDir/output/copyprivate.mp4"
        extractor = MediaExtractor()
        extractor!!.setDataSource(videoFilePath)
        muxer = MediaMuxer(outFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        binding.btnExtractor.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                mediaExtractor()
            }
        }
        binding.btnMuxer.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                mediaMuxer()
            }
        }
    }

    private fun mediaExtractor() {
        showStatus("开始解封装")
        for (i in 0 until extractor!!.trackCount) {
            val mediaFormat = extractor!!.getTrackFormat(i)
            val mime = mediaFormat.getString(MediaFormat.KEY_MIME)?:""
            if (videoHeight == 0 || videoWidth == 0) {
                videoHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
                videoWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH)
                Log.d(javaClass.simpleName, "mediaExtractor: width:${videoWidth},height:${videoHeight}")
            }
            if (mime.startsWith("audio/", true) && audioTrackIndex == -1) {
                audioTrackIndex = muxer!!.addTrack(mediaFormat)
                extractor!!.selectTrack(i) // 去掉这个就只剩下视频
            } else if (mime.startsWith("video/", true) && videoTrackIndex == -1) {
                videoTrackIndex = muxer!!.addTrack(mediaFormat)
                extractor!!.selectTrack(i) // 去掉这个就只剩下音频
            }
        }
        showStatus("解封装完成，videoTraceIndex:${videoTrackIndex}, audioTrackIndex:${audioTrackIndex}")
        Log.d(javaClass.simpleName, "mediaExtractor: 解封装完成，videoTraceIndex:${videoTrackIndex}, audioTrackIndex:${audioTrackIndex}")
    }
    @SuppressLint("WrongConstant")
    private fun mediaMuxer() {
        if (extractor == null) {
            showStatus("extractor is null，请先解封装")
            return
        }
        showStatus("开始封装")
        muxer!!.start()
        val byteBuffer = ByteBuffer.allocate(videoHeight * videoWidth)
        val bufferInfo = MediaCodec.BufferInfo()
        while (true) {
            val readSampleSize = extractor!!.readSampleData(byteBuffer, 0)
            if (readSampleSize < 0) {
                break
            }
            bufferInfo.offset = 0
            bufferInfo.size = readSampleSize
            bufferInfo.presentationTimeUs = extractor!!.sampleTime
            bufferInfo.flags = extractor!!.sampleFlags
            if (extractor!!.sampleTrackIndex == audioTrackIndex) {
                muxer!!.writeSampleData(audioTrackIndex, byteBuffer, bufferInfo)
            } else if (extractor!!.sampleTrackIndex == videoTrackIndex) {
                muxer!!.writeSampleData(videoTrackIndex, byteBuffer, bufferInfo)
            }
            extractor!!.advance()
        }
        release()
        showStatus("封装完成，输出文件为:${outFilePath}")
        Log.d(javaClass.simpleName, "mediaMuxer: 封装完成，输出文件为:${outFilePath}")
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun showStatus(text: String) {
        binding.tvStatus.text = text
    }

    private fun release() {
        muxer?.stop()
        muxer?.release()
        extractor?.release()
    }
}