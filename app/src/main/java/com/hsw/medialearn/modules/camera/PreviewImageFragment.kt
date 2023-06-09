package com.hsw.medialearn.modules.camera

import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.hsw.medialearn.R

/**
 * @author: HSW
 * @data: 2023/6/8
 * @desc:
 */
class PreviewImageFragment constructor(private val uri: Uri?): DialogFragment(R.layout.fragment_image_preview) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.iv_preview)
        imageView.setImageURI(uri)
    }
}