package com.keremk.instakotlinapp.Share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.SharePagerAdapter
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    private val TAG = "Share Activity"
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setupAuthListener()

        requestStorageAndCameraPermission()
    }

    private fun requestStorageAndCameraPermission() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    Log.e("hata", "Tüm izinler verildi")
                    setupShareViewPager()
                }
                if (report.isAnyPermissionPermanentlyDenied) {
                    Log.e("hata", "izinlerden birine bir daha sorma denmiş")
                    var builder = AlertDialog.Builder(applicationContext)
                    builder.setTitle("Izin Gerekli")
                    builder.setMessage("Kullanmaya Devam Etmek için Ayarlardan İzin Vermeniz Gereklidir.")
                    builder.setPositiveButton("Ayarlara Git") { p0, p1 ->
                        p0.cancel()
                        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        var uri = Uri.fromParts("package", packageName, null)
                        intent.setData(uri)
                        startActivity(intent)
                        finish()
                    }
                    builder.setNegativeButton("Iptal") { p0, p1 ->
                        p0.cancel()
                        finish()
                    }
                    builder.show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, token: PermissionToken?) {
                Log.e("hata", "izinlerden biri reddedildi ikna et ")
                var builder = AlertDialog.Builder(applicationContext)
                builder.setTitle("Izin Gerekli")
                builder.setMessage("Uygulamaya izin vermeniz gerekiyor onaylıyor musunuz?")
                builder.setPositiveButton("ONAY VER") { p0, p1 ->
                    p0.cancel()
                    token!!.continuePermissionRequest()
                }
                builder.setNegativeButton("Iptal") { p0, p1 ->
                    p0.cancel()
                    token!!.cancelPermissionRequest()
                    finish()
                }
                builder.show()

            }
        }).withErrorListener { e: DexterError ->

        }.check()
    }

    private fun setupShareViewPager() {
        var tabAdlari = ArrayList<String>()
        tabAdlari.add("GALERİ")
        tabAdlari.add("FOTOĞRAF")
        tabAdlari.add("VİDEO")
        var sharePagerAdapter = SharePagerAdapter(supportFragmentManager, tabAdlari)
        sharePagerAdapter.addFragment(ShareGalleryFragment())
        sharePagerAdapter.addFragment(ShareCameraFragment())
        sharePagerAdapter.addFragment(ShareVideoFragment())
        Log.e(TAG, "setupShareViewPager: Geldi")
        shareViewPager.adapter = sharePagerAdapter
        sharetablayout.setupWithViewPager(shareViewPager)
    }

    override fun onBackPressed() {
        anaLayout.visibility = View.VISIBLE
        fragmentContainerLayout.visibility = View.GONE
        super.onBackPressed()
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    Log.e("HATA", "Kullanıcı oturum açmamış, ProfileActivitydesin")
                    val intent = Intent(
                        applicationContext, LoginActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}