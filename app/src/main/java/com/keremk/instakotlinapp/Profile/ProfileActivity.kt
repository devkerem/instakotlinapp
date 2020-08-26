package com.keremk.instakotlinapp.Profile

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.Models.UserPosts
import com.keremk.instakotlinapp.Models.Users
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.VideoRecyclerView.view.CenterLayoutManager
import com.keremk.instakotlinapp.utils.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.greenrobot.eventbus.EventBus


class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 4
    private val TAG = "ProfileActivity"
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var tumGonderiler: ArrayList<UserPosts>
    var mRecyclerView: AutoPlayVideoRecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList<UserPosts>()

        setupToolbar()
        kullaniciBilgileriniGetir()

        kullaniciPostlariniGetir(mUser.uid)
        imgGrid.setOnClickListener {
            setupRecyclerView(1)
        }
        imgList.setOnClickListener {
            setupRecyclerView(2)
        }

    }

    private fun kullaniciBilgileriniGetir() {

        tvProfilDuzenleButon.isEnabled = false
        imgProfileSettings.isEnabled = false

        mRef.child("users").child(mUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.getValue() != null) {
                    var okunanKullaniciBilgileri = p0.getValue(Users::class.java)

                    EventBus.getDefault().postSticky(EventbusDataEvents.KullaniciBilgileriniGonder(okunanKullaniciBilgileri))
                    tvProfilDuzenleButon.isEnabled = true
                    imgProfileSettings.isEnabled = true
                    tvProfilAdiToolbar.setText(okunanKullaniciBilgileri!!.user_name)
                    tvProfilGercekAdi.setText(okunanKullaniciBilgileri.adi_soyadi)
                    tvFollowerSayisi.setText(okunanKullaniciBilgileri.user_detail!!.follower)
                    tvFollowingSayisi.setText(okunanKullaniciBilgileri.user_detail!!.following)
                    tvPostSayisi.setText(okunanKullaniciBilgileri.user_detail!!.post)

                    var imgUrl: String = okunanKullaniciBilgileri.user_detail!!.profile_picture!!
                    UniversalImageLoader.setImage(imgUrl, circleProfileImage, progressBar, "")

                    if (!okunanKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()) {
                        tvBiyografi.visibility = View.VISIBLE
                        tvBiyografi.setText(okunanKullaniciBilgileri!!.user_detail!!.biography!!)
                    }
                    if (!okunanKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()) {
                        tvWebSitesi.visibility = View.VISIBLE
                        tvWebSitesi.setText(okunanKullaniciBilgileri!!.user_detail!!.web_site!!)
                    }
                }
            }
        })

    }


    private fun setupToolbar() {
        imgProfileSettings.setOnClickListener {
            var intent = Intent(this, ProfileSettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        tvProfilDuzenleButon.setOnClickListener {

            tumlayout.visibility = View.GONE
            profileContainer.visibility = View.VISIBLE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileContainer, ProfileEditFragment())
            transaction.addToBackStack("editProfileFragmentEklendi")
            transaction.commit()

        }

    }


    override fun onResume() {
        setupNavigationView()
        if (mRecyclerView?.handingVideoHolder != null) mRecyclerView!!.handingVideoHolder.playVideo()
        super.onResume()
    }

    override fun onPause() {
        if (mRecyclerView?.handingVideoHolder != null) mRecyclerView!!.handingVideoHolder.stopVideo()
        super.onPause()
    }


    fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationView)
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun kullaniciPostlariniGetir(kullaniciID: String) {
        mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var userID = kullaniciID
                var kullaniciAdi = p0!!.getValue(Users::class.java)!!.user_name
                var kullaniciFotoURL = p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture
                mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0!!.hasChildren()) {
                            Log.e("HATA", "COCUK VAR")
                            for (ds in p0!!.children) {
                                var eklenecekUserPosts = UserPosts()
                                eklenecekUserPosts.userID = userID
                                eklenecekUserPosts.userName = kullaniciAdi
                                eklenecekUserPosts.userPhotoURL = kullaniciFotoURL
                                eklenecekUserPosts.postID = ds.getValue(Posts::class.java)!!.post_id
                                eklenecekUserPosts.postURL = ds.getValue(Posts::class.java)!!.file_url
                                eklenecekUserPosts.postAciklama = ds.getValue(Posts::class.java)!!.aciklama
                                eklenecekUserPosts.postYuklenmeTarih = ds.getValue(Posts::class.java)!!.yuklenme_tarihi
                                tumGonderiler.add(eklenecekUserPosts)
                            }
                        }
                        setupRecyclerView(2)
                    }
                })
            }
        })
    }

    //1 ise grid 2 ise list view şeklinde veriler gösterilir
    private fun setupRecyclerView(layoutCesidi: Int) {

        if (layoutCesidi == 1) {
            if (mRecyclerView?.handingVideoHolder != null) {
                mRecyclerView!!.handingVideoHolder.stopVideo()
            }

            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)

            mRecyclerView = profileRecyclerView
            mRecyclerView!!.setHasFixedSize(true)
            mRecyclerView!!.adapter = ProfilePostGridRecyclerAdapter(tumGonderiler, this)
            mRecyclerView!!.layoutManager = GridLayoutManager(this, 3)
        } else if (layoutCesidi == 2) {
            if (mRecyclerView?.handingVideoHolder != null) {
                mRecyclerView!!.handingVideoHolder.playVideo()
            }

            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            mRecyclerView = profileRecyclerView
            mRecyclerView!!.layoutManager = CenterLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mRecyclerView!!.adapter = ProfilePostListRecyclerAdapter(this, tumGonderiler)

        }

    }

    override fun onBackPressed() {
        tumlayout.visibility = View.VISIBLE
        super.onBackPressed()
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            var user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.e("HATA", "Kullanıcı oturum açmamış, ProfileActivitydesin")
                var intent = Intent(this@ProfileActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("HATA", "ProfileActivitydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}

