package com.keremk.instakotlinapp.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.Models.UserPosts
import com.keremk.instakotlinapp.Models.Users
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.BottomNavigationViewHelper
import com.keremk.instakotlinapp.utils.HomeFragmentRecyclerAdapter
import com.keremk.instakotlinapp.utils.Posts
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    private val ACTIVITY_NO = 0
    lateinit var fragmentView: View
    lateinit var tumGonderiler: ArrayList<UserPosts>
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    var mRecyclerView: AutoPlayVideoRecyclerView? = null
    lateinit var mRef: DatabaseReference
    lateinit var tumTakipEttiklerim: ArrayList<String>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList()
        tumTakipEttiklerim = ArrayList()
        tumTakipEttiklerimiGetir()
        fragmentView.imgTabCamera.setOnClickListener {
            (activity as HomeActivity).homeViewPager.setCurrentItem(0)
        }
        fragmentView.imgTabDirectMessage.setOnClickListener {
            (activity as HomeActivity).homeViewPager.setCurrentItem(2)
        }
        return fragmentView
    }

    private fun tumTakipEttiklerimiGetir() {

        tumTakipEttiklerim.add(mUser.uid)
        //Log.e("HATA9", "benim uidim ekleniyor..." + mUser.uid)

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.getValue() != null) {

                    for (ds in p0!!.children) {
                        tumTakipEttiklerim.add(ds.key!!)
                    }

                    //Log.e("HATA", "TÜM TAKIP ETTİKLERİM:" + tumTakipEttiklerim.toString())
                    kullaniciPostlariniGetir()

                } else {
                    //Log.e("HATA9", "hiç takip ettiğim yok,sadece kendi gönderilerimi görücem")
                    kullaniciPostlariniGetir()
                }


            }

        })

    }

    /*    private fun tumTakipEttiklerimiGetir() {
            tumTakipEttiklerim.add(mUser.uid)
            mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.getValue() != null) {

                        for (ds in p0.children) {
                            tumTakipEttiklerim.add(ds.key!!)
                            kullaniciPostlariniGetir()
                        }
                    } else {

                    }
                }
            })
        }
    */
    private fun kullaniciPostlariniGetir() {

        mRef = FirebaseDatabase.getInstance().reference
        //Log.e("ttt","takip edilecek user liste size:"+tumTakipEttiklerim.size+" liste:"+tumTakipEttiklerim)
        for (i in 0..tumTakipEttiklerim.size - 1) {

            var kullaniciID = tumTakipEttiklerim.get(i)


            mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0!!.getValue() != null) {
                        var userID = kullaniciID
                        var kullaniciAdi = p0!!.getValue(Users::class.java)!!.user_name
                        var kullaniciFotoURL = p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture


                        mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if (p0!!.hasChildren()) {
                                    //Log.e("HATA", kullaniciID + " idli kişinin fotoları var")
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
                                        //Log.e("ttt","kullanıcının tüm gönderileri eklendi, diğer kullanıcıya geç")
                                    }
                                }
                                //Log.e("ttt","gönderiler eklenmiş, tüm kullanıcılar gezilmiş listeyi göster i:"+i+" size:"+tumGonderiler.size)
                                if (tumGonderiler.size > 0 && i == (tumTakipEttiklerim.size - 1)) {
                                    //Log.e("ttt1","gönderiler eklenmiş, tüm kullanıcılar gezilmiş listeyi göster i:"+i+" size:"+tumGonderiler.size)
                                    setupRecyclerView()
                                }
                            }

                        })
                    } else {
                        if (tumGonderiler.size > 0 && i == (tumTakipEttiklerim.size - 1)) {
                            //Log.e("ttt2","gönderiler eklenmiş, tüm kullanıcılar gezilmiş listeyi göster i:"+i+" size:"+tumGonderiler.size)
                            setupRecyclerView()
                        }
                    }

                }
            })
        }
    }

    private fun setupRecyclerView() {
        mRecyclerView = fragmentView.recyclerView
        var recyclerAdapter = HomeFragmentRecyclerAdapter(this.activity!!, tumGonderiler)
        mRecyclerView!!.adapter = recyclerAdapter
        mRecyclerView!!.layoutManager = LinearLayoutManager(this.activity!!, LinearLayoutManager.VERTICAL, false)
        for (i in tumGonderiler) {
            Log.e("*****", i.userPhotoURL!!)
        }

    }

    fun setupNavigationView() {
        var fragmentbottomNaview = fragmentView.bottomNavigationViewEx
        BottomNavigationViewHelper.setupBottomNavigationView(fragmentbottomNaview)
        BottomNavigationViewHelper.setupNavigation(activity!!, fragmentbottomNaview)
        var menu = fragmentbottomNaview.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    Log.e("HATA", "Kullanıcı oturum açmamış, ProfileActivitydesin")
                    var intent = Intent(
                        context, LoginActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity!!.finish()
                } else {
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        setupNavigationView()
        if (mRecyclerView != null && mRecyclerView?.handingVideoHolder != null) mRecyclerView!!.handingVideoHolder.playVideo()
    }

    override fun onPause() {
        super.onPause()
        if (mRecyclerView != null && mRecyclerView?.handingVideoHolder != null) mRecyclerView!!.handingVideoHolder.stopVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRecyclerView?.handingVideoHolder != null) mRecyclerView!!.handingVideoHolder.stopVideo()

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
        if (mRecyclerView?.handingVideoHolder != null) mRecyclerView!!.handingVideoHolder.stopVideo()

    }
}