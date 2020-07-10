package com.keremk.instakotlinapp.Profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_home.*

class ProfileActivity : AppCompatActivity() {
    private val ACTIVITY_NO=4
    private val TAG="Profile Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupNavigationView()
    }

    fun setupNavigationView() {
        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx)
        BottomnavigationViewHelper.setupNavigation(this,bottomNavigationViewEx)
        var menu = bottomNavigationViewEx.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }
}