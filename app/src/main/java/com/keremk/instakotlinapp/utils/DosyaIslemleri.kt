package com.keremk.instakotlinapp.utils

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.iceteck.silicompressorr.SiliCompressor
import com.keremk.instakotlinapp.Profile.YukleniyorFragment
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.Share.ShareNextFragment
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class DosyaIslemleri() {
    companion object {
        fun klasordakiDosyalariGetir(klasorAdi: String): ArrayList<String> {
            var tumDosyalar = ArrayList<String>()
            var file = File(klasorAdi)
            var klasordekiTumDosyalar = file.listFiles()

            if (klasordekiTumDosyalar != null) {
                if (klasordekiTumDosyalar.size > 1) {
                    Arrays.sort(klasordekiTumDosyalar, object : Comparator<File> {
                        override fun compare(o1: File?, o2: File?): Int {
                            if (o1!!.lastModified() > o2!!.lastModified()) {
                                return -1
                            } else return 1
                        }

                    })
                }
                for (i in 0..klasordekiTumDosyalar.size - 1) {
                    if (klasordekiTumDosyalar[i].isFile) {
                        var okunanDosyaYolu = klasordekiTumDosyalar[i].absolutePath
                        var dosyaTuru = okunanDosyaYolu.substring(okunanDosyaYolu.lastIndexOf("."))
                        if (dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4")) {
                            tumDosyalar.add(okunanDosyaYolu)
                            Log.e("HATA", "klasordakiDosyalariGetir: " + okunanDosyaYolu)

                        }
                    }
                }
            }
            return tumDosyalar
        }

        fun getImagesPath(activity: Activity): ArrayList<String?> {
            val uri: Uri
            val listOfAllImages = ArrayList<String?>()
            val cursor: Cursor?
            val column_index_data: Int
            val column_index_folder_name: Int
            var PathOfImage: String? = null
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            cursor = activity.contentResolver.query(
                uri, projection, null, null, null
            )
            column_index_data = cursor!!.getColumnIndexOrThrow(MediaColumns.DATA)
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                PathOfImage = cursor.getString(column_index_data)
                Log.e("HATA", "getImagesPath:: " + PathOfImage)
                listOfAllImages.add(PathOfImage)
            }
            return listOfAllImages
        }

        fun compressResimDosya(fragment: Fragment, secilenResimYolu: String) {
            ResimCompressAsyncTask(fragment).execute(secilenResimYolu)
        }

        fun compressVideoDosya(fragment: ShareNextFragment, secilenDosyaYolu: String) {
            VideoCompressAsyncTask(fragment).execute(secilenDosyaYolu)
        }
    }

    internal class VideoCompressAsyncTask(fragment: Fragment) : AsyncTask<String, String, String>() {
        var myFragment = fragment
        var compressFragment = YukleniyorFragment()
        override fun onPreExecute() {
            compressFragment.show(myFragment.activity!!.supportFragmentManager, "compressDialogBasladi")
            compressFragment.isCancelable = false
        }

        override fun doInBackground(vararg params: String?): String? {
            var appname = myFragment.activity!!.getString(R.string.app_name_without_space)
            var yeniOlusanDosyaninKlasoru = File("/storage/emulated/0/DCIM/" + appname + "/TestKlasor/compressed/")
            if (yeniOlusanDosyaninKlasoru.isDirectory || yeniOlusanDosyaninKlasoru.mkdirs()) {
                var yeniDosyaninPath = SiliCompressor.with(myFragment.context).compressVideo(params[0], yeniOlusanDosyaninKlasoru.path)
                return yeniDosyaninPath
            }
            return null
        }

        override fun onPostExecute(yeniDosyaninPath: String?) {
            if (!yeniDosyaninPath.isNullOrEmpty()){
                compressFragment.dismiss()

                (myFragment as ShareNextFragment).uploadStorage(yeniDosyaninPath)
            }
            super.onPostExecute(yeniDosyaninPath)

        }
    }

    internal class ResimCompressAsyncTask(fragment: Fragment) : AsyncTask<String, String, String>() {
        var myFragment = fragment
        var compressFragment = YukleniyorFragment()
        override fun doInBackground(vararg params: String?): String {
            var appname = myFragment.activity!!.getString(R.string.app_name_without_space)
            var yeniOlusanDosyaninKlasoru = File("/storage/emulated/0/DCIM/" + appname + "/TestKlasor/compressed/")
            var yeniDosyaYolu = SiliCompressor.with(myFragment.context).compress(params[0], yeniOlusanDosyaninKlasoru)
            return yeniDosyaYolu
        }

        override fun onPreExecute() {
            compressFragment.show(myFragment.activity!!.supportFragmentManager, "compressDialogBasladi")
            compressFragment.isCancelable = false
            super.onPreExecute()
        }

        override fun onPostExecute(filePath: String?) {
            compressFragment.dismiss()
            (myFragment as ShareNextFragment).uploadStorage(filePath)
            super.onPostExecute(filePath)
        }
    }
}