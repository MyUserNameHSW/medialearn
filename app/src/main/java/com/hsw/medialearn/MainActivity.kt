package com.hsw.medialearn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.hsw.medialearn.databinding.ActivityMainBinding
import com.hsw.medialearn.modules.audio.AudioActivity
import com.hsw.medialearn.modules.camera.CameraActivity
import com.hsw.medialearn.modules.canvas.CanvasActivity
import com.hsw.medialearn.modules.dealmp4.DealMp4Activity
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val classList = listOf<Pair<String, KClass<out Activity>>>(
        "Canvas" to CanvasActivity::class,
        "Audio" to AudioActivity::class,
        "CameraVideoPrv" to CameraActivity::class,
        "DealMp4" to DealMp4Activity::class,
        "Canvas" to CanvasActivity::class,
        "Canvas" to CanvasActivity::class,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = binding.recyclerView
        val layoutManager = GridLayoutManager(this, 3)
        val verticalDecoration = MaterialDividerItemDecoration(this, LinearLayout.VERTICAL)
        verticalDecoration.setDividerColorResource(this, R.color.color_f4f5f6)
        recyclerView.addItemDecoration(verticalDecoration)
        val horizontalDecoration = MaterialDividerItemDecoration(this, LinearLayout.HORIZONTAL)
        horizontalDecoration.setDividerColorResource(this, R.color.color_f4f5f6)
        recyclerView.addItemDecoration(horizontalDecoration)
        recyclerView.layoutManager = layoutManager
        val adapter = ItemAdapter(classList.toMutableList())
        recyclerView.adapter = adapter
    }

    inner class ItemAdapter(list: MutableList<Pair<String, KClass<out Activity>>>): BaseQuickAdapter<Pair<String, KClass<out Activity>>, BaseViewHolder>(R.layout.vh_menu_item, list) {
        override fun convert(
            holder: BaseViewHolder,
            item: Pair<String, KClass<out Activity>>
        ) {
            holder.setText(R.id.btn_nav, item.first)
            holder.getView<MaterialButton>(R.id.btn_nav).setOnClickListener {
                startActivity(Intent(this@MainActivity, item.second.java))
            }
        }
    }

    /**
     * A native method that is implemented by the 'medialearn' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'medialearn' library on application startup.
        init {
            System.loadLibrary("medialearn")
        }
    }
}