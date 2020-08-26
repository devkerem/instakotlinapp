package com.keremk.instakotlinapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.keremk.instakotlinapp.Models.Konusmalar
import com.keremk.instakotlinapp.Models.Users
import com.keremk.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_satir_konusma_item.view.*

class KonusmalarRecyclerAdapter(var tumKonusmalar:ArrayList<Konusmalar>,var myContext: Context):RecyclerView.Adapter<KonusmalarRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var myView = LayoutInflater.from(myContext).inflate(R.layout.tek_satir_konusma_item,parent,false)
        return MyViewHolder(myView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(tumKonusmalar.get(position))
    }

    override fun getItemCount(): Int {
        return tumKonusmalar.size
    }

    class MyViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tumLayout = itemView as ConstraintLayout
        var enSonAtilanMesaj = tumLayout.tvSonMesaj
        var mesaZaman = tumLayout.tvMesajZaman
        var sohbetEdilenUserName =tumLayout.tvUserName
        var sohbetEdilenUserProfilePicture =tumLayout.imgUserProfilePicture
        fun setData(oankiKonusma: Konusmalar) {
            enSonAtilanMesaj.text = oankiKonusma.son_mesaj.toString()
            mesaZaman.text =TimeAgo.getTimeAgoForComments(oankiKonusma.time!!.toLong())
            sohbetEdilenKullaniciBilgileriniGetir(oankiKonusma.user_id.toString())
        }

        private fun sohbetEdilenKullaniciBilgileriniGetir(userID: String) {
            FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue()!=null){
                        for (ds in snapshot.children){
                            sohbetEdilenUserName.text=snapshot.child("user_name").getValue().toString()
                            var userProfilePictureURL = snapshot.child("user_detail").child("profile_picture").getValue().toString()
                            UniversalImageLoader.setImage(userProfilePictureURL,sohbetEdilenUserProfilePicture,null,"")
                        }
                    }
                }
            })
        }

    }
}