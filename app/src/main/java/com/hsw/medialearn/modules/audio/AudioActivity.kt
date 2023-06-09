package com.hsw.medialearn.modules.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.hsw.medialearn.databinding.ActivityAudioBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream


class AudioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioBinding

    private var mIsRecording = false
    private var mAudioRecord: AudioRecord? = null
    private var mAudioTrack: AudioTrack? = null
    private var mBufferSizeInBytes = 0
    private var mData: ByteArray? = null
    private var mFileOutputStream: FileOutputStream? = null
    private var mFileSize: Long = 0
    private lateinit var filePath: String

    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) {
            // 无权限就关闭
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 文件保存到 data/data/{packageName}/files/record.wav
        filePath = "$filesDir/record.wav"
        launcher.launch(Manifest.permission.RECORD_AUDIO)
        binding.btnPlay.setOnClickListener {
            playAudio()
        }

        binding.btnStartRecord.setOnClickListener {
            startRecording()
        }

        binding.btnStopRecord.setOnClickListener {
            stopRecording()
        }
    }

    private fun createAudioRecord() {
        val audioSource = MediaRecorder.AudioSource.MIC
        val sampleRateInHz = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mAudioRecord = AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mBufferSizeInBytes)
        mData = ByteArray(mBufferSizeInBytes)
    }

    private fun startRecording() {
        createAudioRecord()
        mIsRecording = true
        binding.tvStatus.text = "Recording"
        mAudioRecord?.startRecording()
        kotlin.runCatching {
            mFileOutputStream = FileOutputStream(filePath)
            WavUtils.writeHeader(mFileOutputStream!!, mBufferSizeInBytes)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            while (mIsRecording) {
                val readCount = mAudioRecord!!.read(mData!!, 0, mBufferSizeInBytes)
                if (readCount == AudioRecord.ERROR_BAD_VALUE || readCount == AudioRecord.ERROR_INVALID_OPERATION) {
                    continue
                }
                kotlin.runCatching {
                    mFileOutputStream!!.write(mData!!, 0, readCount)
                    mFileSize += readCount
                }
            }
            releaseRecording()
        }
    }

    private fun stopRecording() {
        mIsRecording = false
        binding.tvStatus.text = "Init"
        kotlin.runCatching {
            if (mFileOutputStream != null) {
                WavUtils.writeDataLength(mFileOutputStream!!.channel, mFileSize)
                mFileOutputStream!!.close()
                mFileOutputStream = null
            }
            mAudioRecord?.stop()
            mAudioRecord?.release()
            mAudioRecord = null
        }
    }

    private fun createAudioTrack() {
        val sampleRateHz = 44100
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        mBufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateHz, channelConfig, audioFormat)
        mAudioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRateHz)
                .setChannelMask(channelConfig)
                .build())
            .build()
    }

    private fun playAudio() {
        createAudioTrack()
        mAudioTrack?.play()
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(filePath)
            val buffer = ByteArray(mBufferSizeInBytes)
            var readCount = fileInputStream!!.read(buffer, 0, mBufferSizeInBytes)
            readCount.also {
                readCount = 2
            }
            while ((readCount.apply {readCount = fileInputStream!!.read(buffer, 0, mBufferSizeInBytes) }) != -1) {
                mAudioTrack?.write(buffer, 0, readCount)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fileInputStream?.let {
                kotlin.runCatching {
                    it.close()
                }
            }
            releaseAudioTrack()
        }
    }

    private fun releaseRecording() {
        mAudioRecord?.let {
            mAudioRecord?.release()
            mAudioRecord = null
        }
    }

    private fun releaseAudioTrack() {
        mAudioTrack?.let {
            mAudioTrack?.release()
            mAudioTrack = null
        }
    }
}