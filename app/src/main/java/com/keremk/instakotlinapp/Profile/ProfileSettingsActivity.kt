package com.keremk.instakotlinapp.Profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {

    private val ACTIVITY_NO=4
    private val TAG="ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        setupNavigationView()
        setupToolbar()
        fragmentNavigations()
    }

    private fun fragmentNavigations() {
        tvProfilDuzenleHesapAyarlari.setOnClickListener {
            profileSettingsRoot.visibility = View.GONE
            var transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingsContainer,ProfileEditFragment())
            transaction.addToBackStack("signOutFragmentEklendi")
            transaction.commit()
        }
        tvCikisYap.setOnClickListener {
            profileSettingsRoot.visibility = View.GONE
            var transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingsContainer,ProfileSignOutFragment())
            transaction.addToBackStack("signOutFragmentEklendi")
            transaction.commit()

        }
    }

    override fun onBackPressed() {
        profileSettingsRoot.visibility = View.VISIBLE
        super.onBackPressed()
    }
    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun setupNavigationView(){
//
        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}