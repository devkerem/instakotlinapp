package com.keremk.instakotlinapp.Generic

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.Models.UserPosts
import com.keremk.instakotlinapp.Models.Users
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.greenrobot.eventbus.EventBus

class UserProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 1
    private val TAG = "SearchActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var tumGonderiler: ArrayList<UserPosts>
    lateinit var secilenUserID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList<UserPosts>()
        secilenUserID = intent.getStringExtra("secilenUserID")!!
        setupButtons()
        kullaniciBilgileriniGetir()

        kullaniciPostlariniGetir(secilenUserID)
        imgGrid.setOnClickListener {
            setupRecyclerView(1)
        }
        imgList.setOnClickListener {
            setupRecyclerView(2)
        }

    }

    private fun kullaniciBilgileriniGetir() {

        tvTakip.isEnabled = false
        imgProfileSettings.isEnabled = false

        mRef.child("users").child(secilenUserID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.getValue() != null) {
                    var okunanKullaniciBilgileri = p0.getValue(Users::class.java)

                    EventBus.getDefault().postSticky(EventbusDataEvents.KullaniciBilgileriniGonder(okunanKullaniciBilgileri))
                    tvTakip.isEnabled = true
                    imgProfileSettings.isEnabled = true
                    tvProfilAdiToolbar.setText(okunanKullaniciBilgileri!!.user_name)
                    tvProfilGercekAdi.setText(okunanKullaniciBilgileri!!.adi_soyadi)
                    tvFollowerSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.follower)
                    tvFollowingSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.following)
                    tvPostSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.post)

                    var imgUrl: String = okunanKullaniciBilgileri!!.user_detail!!.profile_picture!!
                    UniversalImageLoader.setImage(imgUrl, circleProfileImage, progressBar, "")

                    if (!okunanKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()) {
                        tvBiyografi.visibility = View.VISIBLE
                        tvBiyografi.setText(okunanKullaniciBilgileri!!.user_detail!!.biography!!)
                    }
                    if (!okunanKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()) {
                        tvWebSitesi.visibility = View.VISIBLE
                        tvWebSitesi.setText(okunanKullaniciBilgileri!!.user_detail!!.web_site!!)
                    } else {
                        tvWebSitesi.visibility = View.GONE
                    }

                }
                takipBilgisiniGetir()
            }
        })

    }

    private fun takipBilgisiniGetir() {
        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    takibiBirakButonOzellikleri()
                } else {
                    takipEtButonOzellikleri()

                }
            }
        })
    }

    fun takipEtButonOzellikleri() {
        tvTakip.setText("Takip Et")

        tvTakip.setTextColor(ContextCompat.getColor(applicationContext, R.color.beyaz))
        tvTakip.setBackgroundResource(R.drawable.register_button_aktif)
    }

    fun takibiBirakButonOzellikleri() {
        tvTakip.setText("Takip Bırak")
        tvTakip.setTextColor(ContextCompat.getColor(applicationContext, R.color.siyah))
        tvTakip.setBackgroundResource(R.drawable.takip_et_beyaz)
    }


    private fun setupButtons() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
        imgProfileSettings.setOnClickListener {
            Toast.makeText(applicationContext, "img Porfile Setting ", Toast.LENGTH_LONG).show()
        }

        tvTakip.setOnClickListener {
            Toast.makeText(applicationContext, "img Porfile Edit .", Toast.LENGTH_LONG).show()
            mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChild(secilenUserID)) {
                        mRef.child("following").child(mUser.uid).child(secilenUserID).removeValue()
                        mRef.child("follower").child(secilenUserID).child(mUser.uid).removeValue()
                        takipEtButonOzellikleri()
                        takipciSayilariniGuncelle()
                    } else {
                        mRef.child("following").child(mUser.uid).child(secilenUserID).setValue(secilenUserID)
                        mRef.child("follower").child(secilenUserID).child(mUser.uid).setValue(mUser.uid)
                        takibiBirakButonOzellikleri()
                        takipciSayilariniGuncelle()
                    }
                }
            })
        }

    }

    private fun takipciSayilariniGuncelle() {
        mRef = FirebaseDatabase.getInstance().reference
        mRef.child("following").child(mUser.uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                var takipEttikleriminSayisi = snapshot.childrenCount.toString()
                mRef.child("follower").child(secilenUserID).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var takipEdenlerinSayisi = snapshot.childrenCount.toString()
                        mRef.child("users").child(mUser.uid).child("user_detail").child("following").setValue(takipEttikleriminSayisi)
                        mRef.child("users").child(secilenUserID).child("user_detail").child("follower").setValue(takipEdenlerinSayisi)
                    }
                })
            }
        })
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
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
                        setupRecyclerView(1)
                    }
                })
            }
        })
    }

    //1 ise grid 2 ise list view şeklinde veriler gösterilir
    private fun setupRecyclerView(layoutCesidi: Int) {

        if (layoutCesidi == 1) {
            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)

            var kullaniciPostListe = profileRecyclerView
            kullaniciPostListe.adapter = ProfilePostGridRecyclerAdapter(tumGonderiler, this)
            kullaniciPostListe.layoutManager = GridLayoutManager(this, 3)
        } else if (layoutCesidi == 2) {
            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            var kullaniciPostListe = profileRecyclerView
            kullaniciPostListe.adapter = ProfilePostListRecyclerAdapter(this, tumGonderiler)

            kullaniciPostListe.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            var user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.e("HATA", "Kullanıcı oturum açmamış, ProfileActivitydesin")
                var intent = Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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