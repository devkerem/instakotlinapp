package com.keremk.instakotlinapp.utils

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class DosyaIslemleri() {
    companion object {
        fun klasordakiDosyalariGetir(klasorAdi: String): ArrayList<String> {
            var tumDosyalar = ArrayList<String>()
            var file = File(klasorAdi)
            var klasordekiTumDosyalar = file.listFiles()

            if (klasordekiTumDosyalar != null) {
                if (klasordekiTumDosyalar.size>1){
                    Arrays.sort(klasordekiTumDosyalar,object :Comparator<File>{
                        override fun compare(o1: File?, o2: File?): Int {
                            if (o1!!.lastModified()>o2!!.lastModified()){
                                return -1
                            }else return 1 
                        }

                    })
                }
                for (i in 0..klasordekiTumDosyalar.size-1){
                    if (klasordekiTumDosyalar[i].isFile){
                        var okunanDosyaYolu = klasordekiTumDosyalar[i].absolutePath
                        var dosyaTuru = okunanDosyaYolu.substring(okunanDosyaYolu.lastIndexOf("."))
                        if (dosyaTuru.equals(".jpg")||dosyaTuru.equals(".jpeg")||dosyaTuru.equals(".png")||dosyaTuru.equals(".mp4")){
                            tumDosyalar.add(okunanDosyaYolu)
                            Log.e("HATA", "klasordakiDosyalariGetir: "+okunanDosyaYolu )
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
                MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            cursor = activity.contentResolver.query(
                uri, projection, null,
                null, null
            )
            column_index_data = cursor!!.getColumnIndexOrThrow(MediaColumns.DATA)
            column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                PathOfImage = cursor.getString(column_index_data)
                Log.e("HATA", "getImagesPath:: "+PathOfImage )
                listOfAllImages.add(PathOfImage)
            }
            return listOfAllImages
        }
    }
}