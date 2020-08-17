package com.keremk.instakotlinapp.Profile

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.R

class ProfileSignOutFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.e("*******", "onCreateDialog: " )
        var alert = AlertDialog.Builder(this!!.activity!!)
            .setTitle("Instagram'dan Çıkış Yap")
            .setMessage("Emin misiniz ?")
            .setPositiveButton("Çıkış Yap", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(activity!!,LoginActivity::class.java))
                }

            })
            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dismiss()
                }
            }).create()

        return alert
    }
}