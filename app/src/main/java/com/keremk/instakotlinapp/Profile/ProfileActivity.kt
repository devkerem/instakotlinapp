package com.keremk.instakotlinapp.Profile
import android.content.Intent
import android.os.Bundle
import com.keremk.instakotlinapp.R
import android.support.v7.app.AppCompatActivity
import com.keremk.instakotlinapp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private val ACTIVITY_NO=4
    private val TAG="Profile Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupToolbar()
        setupNavigationView()
    }

    private fun setupToolbar() {
        imgProfileSettings.setOnClickListener {
            var intent= Intent(this,ProfileSettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
    }
    fun setupNavigationView(){
        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}