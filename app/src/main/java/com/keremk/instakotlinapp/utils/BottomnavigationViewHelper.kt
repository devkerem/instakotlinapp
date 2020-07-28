package com.keremk.instakotlinapp.utils

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.MenuItem
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.keremk.instakotlinapp.Home.HomeActivity
import com.keremk.instakotlinapp.News.NewsActivity
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.Search.SearchActivity
import com.keremk.instakotlinapp.Share.ShareActivity
import com.keremk.instakotlinapp.Profile.ProfileActivity

class BottomNavigationViewHelper {
    companion object {
        fun setupBottomNavigationView(bottomnavigationViewEx: BottomNavigationViewEx) {
            bottomnavigationViewEx.enableItemShiftingMode(true)
            bottomnavigationViewEx.enableShiftingMode(false)
            bottomnavigationViewEx.setTextVisibility(false)
            bottomnavigationViewEx.enableAnimation(true)
        }

        fun setupNavigation(context: Context, bottomnavigationViewEx: BottomNavigationViewEx) {
            bottomnavigationViewEx.onNavigationItemSelectedListener =
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {
                        when (item.itemId) {
                            R.id.ic_home -> {
                                val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_search -> {
                                val intent = Intent(context, SearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_share -> {
                                val intent = Intent(context, ShareActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_news -> {
                                val intent = Intent(context, NewsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_profile -> {
                                val intent = Intent(context, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                        }
                        return false
                    }
                }
        }
    }
}
