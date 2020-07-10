package com.keremk.instakotlinapp.Home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.BottomnavigationViewHelper
import com.keremk.instakotlinapp.utils.HomePagerAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 0
    private val TAG = "Home Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupNavigationView()
        setupHomeViewPager()
    }

    fun setupHomeViewPager() {
        var homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(CameraFragment())
        homePagerAdapter.addFragment(HomeFragment())
        homePagerAdapter.addFragment(MessagesFragment())
        homeViewPager.adapter = homePagerAdapter
        homeViewPager.currentItem=1
    }

    fun setupNavigationView() {
        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationViewEx)
        var menu = bottomNavigationViewEx.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}