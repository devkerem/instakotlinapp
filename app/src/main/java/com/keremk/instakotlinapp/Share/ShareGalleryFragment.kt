package com.keremk.instakotlinapp.Share

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.*
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ShareGalleryFragment : Fragment() {

    var secilenDosyaYolu: String? = null
    var dosyaTuruResimMi: Boolean? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_share_gallery, container, false)
        var klasorPath = ArrayList<String>()
        var klasorAdlari = ArrayList<String>()
        val yol = activity!!.applicationContext.getExternalFilesDir(null)!!.absolutePath
        Log.e("HATA", "yol onCreateView: " + yol)
        var kameraResimleri = "/storage/emulated/0/DCIM/Camera/"
        var indirilenResimler = "/storage/emulated/0/Download"
        var whatsAppResimleri = "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/"
        klasorPath.add(kameraResimleri)
        klasorPath.add(indirilenResimler)
        klasorPath.add(whatsAppResimleri)
        klasorAdlari.add("Kamera")
        klasorAdlari.add("İndirilenler")
        klasorAdlari.add("Whatsapp")

        var spinnerArrayAdapter = ArrayAdapter(
            activity!!.applicationContext, android.R.layout.simple_spinner_item, klasorAdlari
        )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spnKlasorAdlari.adapter = spinnerArrayAdapter
        view.spnKlasorAdlari.setSelection(0)
        view.spnKlasorAdlari.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
//                setupGridView(DosyaIslemleri.klasordakiDosyalariGetir(klasorPath.get(position)))
                setupRecyclerView(DosyaIslemleri.klasordakiDosyalariGetir(klasorPath.get(position)))
            }

        }

        view.tvIleriButton.setOnClickListener {
            activity!!.anaLayout.visibility = View.GONE
            activity!!.fragmentContainerLayout.visibility = View.VISIBLE
            var transaction = activity!!.supportFragmentManager.beginTransaction()
            EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(secilenDosyaYolu, dosyaTuruResimMi))
            videoView.stopPlayback()
            transaction.replace(R.id.fragmentContainerLayout, ShareNextFragment())
            transaction.addToBackStack("editProfileFragmentEklendii")
            transaction.commit()

        }
        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    private fun setupRecyclerView(klasordakiDosyalar: ArrayList<String>) {
        var recyclerAdapter = ShareGalleryRecyclerAdapter(klasordakiDosyalar,this.activity!!)
        recyclerViewDosyalar.adapter =recyclerAdapter
        var layoutManager= GridLayoutManager(context,4)
        recyclerViewDosyalar.setHasFixedSize(true)
        recyclerViewDosyalar.setItemViewCacheSize(30)
        recyclerViewDosyalar.setDrawingCacheEnabled(true)
        recyclerViewDosyalar.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW)
        recyclerViewDosyalar.layoutManager = layoutManager
        secilenDosyaYolu = klasordakiDosyalar.get(0)
        resimVeyaVideoGoster(secilenDosyaYolu!!)
    }



    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe  internal fun onSecilenDosyaEvent(secilenDosya: EventbusDataEvents.GalerySecilenDosyaYolunuGonder) {
        secilenDosyaYolu = secilenDosya.dosyaYolu!!
        resimVeyaVideoGoster(secilenDosyaYolu!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun resimVeyaVideoGoster(dosyaYolu: String) {
        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))
        if (dosyaTuru.equals(".mp4")) {
            videoView.visibility = View.VISIBLE
            imageCropView.visibility = View.GONE
            videoView.setVideoURI(Uri.parse("file://" + dosyaYolu))
            videoView.start()
            dosyaTuruResimMi = false
        } else {
            videoView.visibility = View.GONE
            imageCropView.visibility = View.VISIBLE
            dosyaTuruResimMi = true
              UniversalImageLoader.setImage(dosyaYolu, imageCropView, null, "file://")
        }
    }
}