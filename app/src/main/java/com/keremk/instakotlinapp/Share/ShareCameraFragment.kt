package com.keremk.instakotlinapp.Share

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.keremk.instakotlinapp.R
import kotlinx.android.synthetic.main.fragment_share_camera.view.*

class ShareCameraFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view :View =inflater.inflate(R.layout.fragment_share_camera, container, false)
        return view

    }

}