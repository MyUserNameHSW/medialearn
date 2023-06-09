package com.hsw.medialearn.modules.opengl.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hsw.medialearn.R
import com.hsw.medialearn.databinding.FragmentShapeBinding
import com.hsw.viewbinding_ktx.viewBinding


class ShapeFragment : Fragment(R.layout.fragment_shape) {
    private val binding by viewBinding<FragmentShapeBinding>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}