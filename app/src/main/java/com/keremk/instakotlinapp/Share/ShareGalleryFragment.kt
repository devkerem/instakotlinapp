package com.keremk.instakotlinapp.Share

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.DosyaIslemleri
import com.keremk.instakotlinapp.utils.ShareActivityGridViewAdapter
import com.keremk.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import java.text.FieldPosition

class ShareGalleryFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_share_gallery, container, false)
        var klasorPath = ArrayList<String>()
        var klasorAdlari = ArrayList<String>()
        var root = Environment.getExternalStorageDirectory().parent
        val yol = activity!!.applicationContext.getExternalFilesDir(null)!!.absolutePath
        Log.e("HATA", "yol onCreateView: " + yol)
        var kameraResimleri =   "/storage/emulated/0/DCIM/Camera/"
        var indirilenResimler = "/storage/emulated/0/Download"
        var whatsAppResimleri = "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/"
        klasorPath.add(kameraResimleri)
        klasorPath.add(indirilenResimler)
        klasorPath.add(whatsAppResimleri)
        klasorAdlari.add("Kamera")
        klasorAdlari.add("İndirilenler")
        klasorAdlari.add("Whatsapp")

        var spinnerArrayAdapter = ArrayAdapter(
            activity!!.applicationContext,
            android.R.layout.simple_spinner_item,
            klasorAdlari
        )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spnKlasorAdlari.adapter = spinnerArrayAdapter
        view.spnKlasorAdlari.onItemSelectedListener= object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setupGridView(DosyaIslemleri.klasordakiDosyalariGetir(klasorPath.get(position)))
            }

        }

        return view
    }
    fun setupGridView(secilenKlasordekiDosyalar: ArrayList<String>){
        var gridAdapter = ShareActivityGridViewAdapter(activity!!.applicationContext,R.layout.tek_sutun_grid_resim,secilenKlasordekiDosyalar)
        gridResimler.adapter = gridAdapter
        gridResimler.setOnItemClickListener(object :AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                UniversalImageLoader.setImage(secilenKlasordekiDosyalar.get(position),imgBuyukResim,null,"file://")
            }

        })
    }
}