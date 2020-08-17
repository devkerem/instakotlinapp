package com.keremk.instakotlinapp.utils

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.keremk.instakotlinapp.Generic.CommentFragment
import com.keremk.instakotlinapp.Home.HomeActivity
import com.keremk.instakotlinapp.Models.UserPosts
import com.keremk.instakotlinapp.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.tek_post_recycler_item.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class HomeFragmentRecyclerAdapter(var context: Context, var tumGonderiler: ArrayList<UserPosts>) : RecyclerView.Adapter<HomeFragmentRecyclerAdapter.ViewHolder>() {
    init {
        Collections.sort(tumGonderiler, object : Comparator<UserPosts> {
            override fun compare(o1: UserPosts?, o2: UserPosts?): Int {
                if (o1!!.postYuklenmeTarih!! > o2!!.postYuklenmeTarih!!) {
                    return -1
                } else {
                    return 1
                }
            }

        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var viewHolder = LayoutInflater.from(context).inflate(R.layout.tek_post_recycler_item, parent, false)
        return ViewHolder(viewHolder, context)
    }

    override fun getItemCount(): Int {
        return tumGonderiler.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(position, tumGonderiler.get(position))
    }

    class ViewHolder(itemView: View, myHomeActivity: Context) : RecyclerView.ViewHolder(itemView) {
        var tumLayout = itemView as ConstraintLayout
        var profileImage = tumLayout.imgUserProfile
        var usernameTitle = tumLayout.tvKullaniciAdiBaslik
        var gonderi = tumLayout.imgPostResim
        var userNameveAciklama = tumLayout.tvKullaniciAdiveAciklama
        var gonderiKacZamanOnce = tumLayout.tvKacZamanOnce
        var yorumYap = tumLayout.imgYorum
        var gonderiBegen = tumLayout.imgBegen
        var myHomeActivity = myHomeActivity
        var mInstaLikeView = tumLayout.insta_like_view
        var begenmeSayisi = tumLayout.tvBegenmeSayisi
        fun setData(position: Int, oAnkiGonderi: UserPosts) {
            usernameTitle.setText(oAnkiGonderi.userName)
            UniversalImageLoader.setImage(oAnkiGonderi.postURL!!, gonderi, null, "")
            UniversalImageLoader.setImage(oAnkiGonderi.userPhotoURL!!, profileImage, null, "")
            var userNameveAciklamaText="<font color=#000>"+oAnkiGonderi.userName.toString()+"</font>"+" "+oAnkiGonderi.postAciklama
            var sonuc:Spanned?=null
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                sonuc= Html.fromHtml(userNameveAciklamaText,Html.FROM_HTML_MODE_LEGACY)
            }else {
                sonuc=Html.fromHtml(userNameveAciklamaText)
            }
            userNameveAciklama.setText(sonuc)
            gonderiKacZamanOnce.setText(TimeAgo.getTimeAgo(oAnkiGonderi.postYuklenmeTarih!!))
            begeniKontrol(oAnkiGonderi)
            yorumYap.setOnClickListener {
                EventBus.getDefault().postSticky(EventbusDataEvents.YorumYapilacakGonderininIDsiniGonder(oAnkiGonderi!!.postID))
                (myHomeActivity as HomeActivity).homeViewPager.visibility = View.GONE
                (myHomeActivity as HomeActivity).homeFragmentContainer.visibility = View.VISIBLE
                val transaction = (myHomeActivity as HomeActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.homeFragmentContainer, CommentFragment())
                transaction.addToBackStack("commentFragmentEklendi")
                transaction.commit()
            }
            gonderiBegen.setOnClickListener {
                var mRef = FirebaseDatabase.getInstance().reference
                val userID = FirebaseAuth.getInstance().currentUser!!.uid
                mRef.child("likes").child(oAnkiGonderi.postID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0!!.hasChild(userID)) {
                            mRef.child("likes").child(oAnkiGonderi.postID!!).child(userID).removeValue()
                            gonderiBegen.setImageResource(R.drawable.ic_like)
                        } else {
                            mRef.child("likes").child(oAnkiGonderi.postID!!).child(userID).setValue(userID)
                            gonderiBegen.setImageResource(R.drawable.ic_begen_kirmizi)
                            mInstaLikeView.start()
                            begenmeSayisi.visibility=View.VISIBLE
                            begenmeSayisi.setText(""+p0!!.childrenCount!!.toString()+" beğenme")
                        }
                    }
                })
            }
            var ilkTiklama:Long = 0
            var sonTiklama:Long = 0
            gonderi.setOnClickListener {
                ilkTiklama = sonTiklama
                sonTiklama = System.currentTimeMillis()
                if (sonTiklama-ilkTiklama<300){
                    mInstaLikeView.start()
                    FirebaseDatabase.getInstance().reference.child("likes").child(oAnkiGonderi.postID!!)
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                }
            }
        }

        fun begeniKontrol(oAnkiGonderi: UserPosts) {
            var mRef = FirebaseDatabase.getInstance().reference
            var userId = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("likes").child(oAnkiGonderi.postID!!).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot!!.getValue()!=null){
                        begenmeSayisi.visibility=View.VISIBLE
                        begenmeSayisi.setText(""+snapshot!!.childrenCount!!.toString()+" beğenme")
                    }else {
                        begenmeSayisi.visibility=View.GONE
                    }

                    if (snapshot.hasChild(userId)) {
                        gonderiBegen.setImageResource(R.drawable.ic_begen_kirmizi)
                    }else{
                        gonderiBegen.setImageResource(R.drawable.ic_like)
                    }
                }
            })
        }
    }
}