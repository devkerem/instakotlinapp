package com.keremk.instakotlinapp.Profile

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.keremk.instakotlinapp.R

class ProfileSignOutFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var alert = AlertDialog.Builder(this!!.activity!!)
            .setTitle("Instagram'dan Çıkış Yap")
            .setMessage("Emin misiniz ?")
            .setPositiveButton("Çıkış Yap", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    FirebaseAuth.getInstance().signOut()
                    activity!!.finish()
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