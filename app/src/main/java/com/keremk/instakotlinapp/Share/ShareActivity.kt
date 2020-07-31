package com.keremk.instakotlinapp.Share

import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.SharePagerAdapter
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {
    private val ACTIVITY_NO=2
    private val TAG="Share Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setupShareViewPager()
    }

    private fun setupShareViewPager() {
        var tabAdlari = ArrayList<String>()
        tabAdlari.add("GALERİ")
        tabAdlari.add("FOTOĞRAF")
        tabAdlari.add("VİDEO")
        var sharePagerAdapter = SharePagerAdapter(supportFragmentManager,tabAdlari)
        sharePagerAdapter.addFragment(ShareGalleryFragment())
        sharePagerAdapter.addFragment(ShareCameraFragment())
        sharePagerAdapter.addFragment(ShareVideoFragment())
        Log.e(TAG, "setupShareViewPager: Geldi" )
        shareViewPager.adapter = sharePagerAdapter
        sharetablayout.setupWithViewPager(shareViewPager)
    }


}