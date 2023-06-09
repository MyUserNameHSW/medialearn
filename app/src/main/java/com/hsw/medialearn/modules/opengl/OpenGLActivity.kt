package com.hsw.medialearn.modules.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import com.hsw.medialearn.R
import com.hsw.medialearn.databinding.ActivityOpenGlBinding
import com.hsw.medialearn.modules.opengl.fragment.BitmapFragment
import com.hsw.medialearn.modules.opengl.fragment.ShapeFragment
import com.hsw.viewbinding_ktx.viewBinding

class OpenGLActivity : AppCompatActivity() {
    private val binding by viewBinding<ActivityOpenGlBinding>()

    private val spArray = SparseArray<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnShape.setOnClickListener {
            showFragment(0)
        }

        binding.btnBitmap.setOnClickListener {
            showFragment(1)
        }
    }

    private fun showFragment(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = spArray[index] ?: instanceFragment(index)
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    private fun instanceFragment(index: Int): Fragment {
        return when (index) {
            0 -> ShapeFragment()
            1 -> BitmapFragment()
            else -> throw IllegalArgumentException()
        }
    }
}