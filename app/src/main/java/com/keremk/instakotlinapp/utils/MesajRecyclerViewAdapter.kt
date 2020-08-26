package com.keremk.instakotlinapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.keremk.instakotlinapp.Models.Mesaj
import com.keremk.instakotlinapp.Models.Users
import com.keremk.instakotlinapp.R
import de.hdodenhof.circleimageview.CircleImageView

class MesajRecyclerViewAdapter(var tumMesajlar: ArrayList<Mesaj>, var myContext: Context, var sohbetEdilecekUser: Users) : RecyclerView.Adapter<MesajRecyclerViewAdapter.MyMesajViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMesajViewHolder {
        var myView: View? = null
        if (viewType == 1) {

            myView = LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_gonderen, parent, false)
            return MyMesajViewHolder(myView, null)
        } else {
            myView = LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_alan, parent, false)
            return MyMesajViewHolder(myView, sohbetEdilecekUser)

        }
    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }

    override fun getItemViewType(position: Int): Int {
        if (tumMesajlar.get(position).user_id == FirebaseAuth.getInstance().currentUser!!.uid) {
            return 1
        } else {
            return 2
        }
    }

    override fun onBindViewHolder(holder: MyMesajViewHolder, position: Int) {
        holder.setData(tumMesajlar.get(position))
    }

    class MyMesajViewHolder(itemView: View, var sohbetEdilecekUser: Users?) : RecyclerView.ViewHolder(itemView) {
        var tumLayout = itemView as ConstraintLayout
        var mesajText = tumLayout.findViewById<TextView>(R.id.tvMesaj)
        var profilePicture = tumLayout.findViewById<CircleImageView>(R.id.mesajUserProfilePic)
        fun setData(oankiMesaj: Mesaj) {
            if (sohbetEdilecekUser != null) {
                UniversalImageLoader.setImage(sohbetEdilecekUser!!.user_detail!!.profile_picture!!,profilePicture,null,"")
            }
            mesajText.setText(oankiMesaj.mesaj)
        }
    }
}