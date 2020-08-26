package com.keremk.instakotlinapp.utils


import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.keremk.instakotlinapp.Models.UserPosts
import com.keremk.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim_profil.view.*

class ProfilePostGridRecyclerAdapter(var kullaniciPostlari: ArrayList<UserPosts>, var myContext: Context) : RecyclerView.Adapter<ProfilePostGridRecyclerAdapter.MyViewHolder>() {

    lateinit var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(myContext)
    }

    override fun getItemCount(): Int {

        return kullaniciPostlari.size

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var tekSutunDosya = inflater.inflate(R.layout.tek_sutun_grid_resim_profil, parent, false)
        return MyViewHolder(tekSutunDosya)

    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var dosyaYolu = kullaniciPostlari.get(position).postURL
        var noktaninGectigiIndex = dosyaYolu!!.lastIndexOf(".")
        var dosyaTuru = dosyaYolu!!.substring(noktaninGectigiIndex, noktaninGectigiIndex + 4)
        if (dosyaTuru.equals(".mp4")) {
            Log.e("DOSYA TURU", "DOSYA TURU" + dosyaTuru)
            holder.videoIcon.visibility = View.VISIBLE
            VideodanThumbOlustur(holder).execute(dosyaYolu)
//            var thumnnailFoto = videodanThumbnailOlustur(dosyaYolu)
//            holder.dosyaResim.setImageBitmap(thumnnailFoto)
//            holder.dosyaProgressBar.visibility = View.GONE
        } else {
            Log.e("DOSYA TURU", "DOSYA TURU" + dosyaTuru)
            holder.videoIcon.visibility = View.GONE
            holder.dosyaProgressBar.visibility = View.VISIBLE
            UniversalImageLoader.setImage(dosyaYolu, holder.dosyaResim, holder.dosyaProgressBar, "")
        }

    }

    class VideodanThumbOlustur(var holder: MyViewHolder) : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg p0: String?): Bitmap {
            var videoPath=p0[0]
            var bitmap: Bitmap? = null
            var mediaMetadataRetriever: MediaMetadataRetriever? = null
            try {
                mediaMetadataRetriever = MediaMetadataRetriever()
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(videoPath, HashMap())
                else {
                    mediaMetadataRetriever.setDataSource(videoPath)
                }
                bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
            } catch (e: Exception) {
                e.printStackTrace()
                throw Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)

            } finally {
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release()
                }
            }
            return bitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            holder.dosyaResim.setImageBitmap(result)
            holder.dosyaProgressBar.visibility = View.GONE
        }

        override fun onPreExecute() {
            super.onPreExecute()
            holder.dosyaProgressBar.visibility = View.VISIBLE
        }

    }

//    @Throws(Throwable::class)
//    fun videodanThumbnailOlustur(videoPath: String): Bitmap? {
//    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tekSutunDosya = itemView as ConstraintLayout

        var dosyaResim = tekSutunDosya.imgTekSutunImage
        var videoIcon = tekSutunDosya.imgVideoIcon
        var dosyaProgressBar = tekSutunDosya.progressBar

    }

    /*fun convertDuration(duration: Long): String {
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
*/

}
