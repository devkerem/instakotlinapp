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
    lateinit var mRef: DatabaseReference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList()
        kullaniciPostlariniGetir(mUser.uid)
        fragmentView.imgTabCamera.setOnClickListener {
            (activity as HomeActivity).homeViewPager.setCurrentItem(0)
        }
        fragmentView.imgTabDirectMessage.setOnClickListener {
            (activity as HomeActivity).homeViewPager.setCurrentItem(2)
        }
        return fragmentView
    }

    private fun kullaniciPostlariniGetir(kullaniciID: String) {
        mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userID = kullaniciID
                var kullaniciAdi = snapshot.getValue(Users::class.java)!!.user_name
                var kullaniciPhotoURL = snapshot.getValue(Users::class.java)!!.user_detail!!.profile_picture
                mRef.child("posts").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChildren()) {
                            for (ds in snapshot.children) {
                                var eklenecekUserPosts = UserPosts()
                                eklenecekUserPosts.userID = userID
                                eklenecekUserPosts.userName = kullaniciAdi
                                eklenecekUserPosts.userPhotoURL = kullaniciPhotoURL
                                eklenecekUserPosts.postID = ds.getValue(Posts::class.java)!!.post_id
                                eklenecekUserPosts.postURL = ds.getValue(Posts::class.java)!!.file_url
                                eklenecekUserPosts.postAciklama = ds.getValue(Posts::class.java)!!.aciklama
                                eklenecekUserPosts.postYuklenmeTarih = ds.getValue(Posts::class.java)!!.yuklenme_tarihi
                                tumGonderiler.add(eklenecekUserPosts)
                            }
                        }

                        setupRecyclerView()
                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupRecyclerView() {
        var recyclerView=fragmentView.recyclerView
         var recyclerAdapter = HomeFragmentRecyclerAdapter(this.activity!!,tumGonderiler)
        recyclerView.adapter=recyclerAdapter
        recyclerView.layoutManager=LinearLayoutManager(this.activity!!,LinearLayoutManager.VERTICAL,true)


    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
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