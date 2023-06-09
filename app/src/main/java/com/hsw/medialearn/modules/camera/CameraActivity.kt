package com.hsw.medialearn.modules.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.hsw.medialearn.R
import com.hsw.medialearn.databinding.ActivityCarmeraBinding
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarmeraBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null

    private var recording: Recording? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) {
            // 无权限就关闭
            finish()
        } else {
            startCamera()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarmeraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        launcher.launch(Manifest.permission.CAMERA)
        binding.btnImageCapture.setOnClickListener {
            takePhoto()
        }
        binding.btnVideoCapture.setOnClickListener {
            captureVideo()
        }
    }

    @SuppressLint("LogConditional")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // 用于将相机的生命周期绑定到生命周期所有者
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // 构建 preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.prvView.surfaceProvider)
                }

            // 选择后置摄像头作为默认设置
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // 搭载 Android 10 或更低版本的设备无法同时实现预览、图片拍摄和图片分析
            imageCapture = ImageCapture.Builder().build()
//            val imageAnalysis = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    it.setAnalyzer(ContextCompat.getMainExecutor(this), CustomerAnalyzer { average ->
//                        Log.d("TAG", "Average luminosity: $average")
//                    })
//                }
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
            try {
                // 在重新绑定之前取消绑定用例
                cameraProvider.unbindAll()
                // 将用例绑定到相机
                cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview, imageCapture, imageAnalysis)
                                        this, cameraSelector, preview, videoCapture, imageCapture)

            } catch(exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // 创建带时间戳的名称和MediaStore条目。
        val name = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // 创建包含文件+元数据的输出选项对象
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // 监听拍照
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    // 预览
                    PreviewImageFragment(output.savedUri).show(supportFragmentManager, "")
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAG", msg)
                }
            }
        )
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.btnVideoCapture.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@CameraActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.btnVideoCapture.apply {
                            text = "Stop Capture"
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(javaClass.simpleName, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(javaClass.simpleName, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        binding.btnVideoCapture.apply {
                            text = "Start Capture"
                            isEnabled = true
                        }
                    }
                }
            }
    }
}