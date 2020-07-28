package com.keremk.instakotlinapp.Search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_home.*

class SearchActivity : AppCompatActivity() {
    private val ACTIVITY_NO=1
    private val TAG="Search Activity"
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