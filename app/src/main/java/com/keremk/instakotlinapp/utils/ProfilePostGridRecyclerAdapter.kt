package com.keremk.instakotlinapp.utils


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.keremk.instakotlinapp.Models.UserPosts
import com.keremk.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim.view.*
class ProfilePostGridRecyclerAdapter(var kullaniciPostlari:ArrayList<UserPosts>, var myContext:Context): RecyclerView.Adapter<ProfilePostGridRecyclerAdapter.MyViewHolder>() {

    lateinit var inflater:LayoutInflater

    init {
        inflater= LayoutInflater.from(myContext)
    }

    override fun getItemCount(): Int {

        return kullaniciPostlari.size

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var tekSutunDosya=inflater.inflate(R.layout.tek_sutun_grid_resim, parent, false)

        return MyViewHolder(tekSutunDosya)

    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var dosyaYolu= kullaniciPostlari.get(position).postURL
        //var dosyaTuru=dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        holder.videoSure.visibility=View.GONE
        UniversalImageLoader.setImage(dosyaYolu!!, holder.dosyaResim, holder.dosyaProgressBar,"")



    }


    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSutunDosya=itemView as ConstraintLayout

        var dosyaResim=tekSutunDosya.imgTekSutunImage
        var videoSure=tekSutunDosya.tvSure
        var dosyaProgressBar=tekSutunDosya.progressBar



    }

    fun convertDuration(duration: Long): String {
        val second = duration / 1000 % 60
        val minute = duration / (1000 * 60) % 60
        val hour = duration / (1000 * 60 * 60) % 24

        var time=""
        if(hour>0){
            time = String.format("%02d:%02d:%02d", hour, minute, second)
        }else {
            time = String.format("%02d:%02d", minute, second)
        }

        return time

    }


}
