package com.keremk.instakotlinapp.News

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_home.*

class NewsActivity : AppCompatActivity() {
    private val ACTIVITY_NO=3
    private val TAG="News Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupNavigationView()
    }

    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationViewEx)
        var menu = bottomNavigationViewEx.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}