package com.keremk.instakotlinapp.Share

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.keremk.instakotlinapp.R
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import kotlinx.android.synthetic.main.fragment_share_video.view.*

class ShareVideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_share_video, container, false)
        return view
    }

}