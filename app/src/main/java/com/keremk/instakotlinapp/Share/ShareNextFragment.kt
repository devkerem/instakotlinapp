package com.keremk.instakotlinapp.Share

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.keremk.instakotlinapp.Home.HomeActivity
import com.keremk.instakotlinapp.Profile.YukleniyorFragment
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.DosyaIslemleri
import com.keremk.instakotlinapp.utils.EventbusDataEvents
import com.keremk.instakotlinapp.utils.Posts
import com.keremk.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.fragment_share_next.*
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ShareNextFragment : Fragment() {
    var secilenDosyaYolu: String? = null
    var dosyaTuruResimMi: Boolean? = null

    //    lateinit var photoURI: Uri
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser

    lateinit var mRef: DatabaseReference
    lateinit var mStorageReference: StorageReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_share_next, container, false)
        UniversalImageLoader.setImage(secilenDosyaYolu!!, view.imgSecilenResim, null, "file://")
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        view.findViewById<View>(R.id.tvBilgi)
        mStorageReference = FirebaseStorage.getInstance().reference
        view.tvIleriButton.setOnClickListener {

            //resim dosyasını sıkıştır
            if (dosyaTuruResimMi == true) {
                DosyaIslemleri.compressResimDosya(this, secilenDosyaYolu!!)
            } else if (dosyaTuruResimMi == false) {
                DosyaIslemleri.compressVideoDosya(this, secilenDosyaYolu!!)
            }

        }
        view.imgClose.setOnClickListener {
            this.activity!!.onBackPressed()
        }
        return view
    }

    private fun veritabaninaBilgileriYaz(downloadUrl: String) {

        var postId = mRef.child("posts").child(mUser.uid).push().key
        var yuklenenPost = Posts(mUser.uid, postId, 0, etpostAciklama.text.toString(), downloadUrl)
        mRef.child("posts").child(mUser.uid).child(postId!!).setValue(yuklenenPost)
        mRef.child("posts").child(mUser.uid).child(postId!!).child("yuklenme_tarihi").setValue(ServerValue.TIMESTAMP)
        //gönderi aciklamasını yorum dugumune ekleyelim
        if (etpostAciklama.text.toString().isNullOrEmpty()) {
//            var yorumKey = mRef.child("comments").child(postId).push().key
            mRef.child("comments").child(postId).child(postId).child("user_id").setValue(mUser.uid)
            mRef.child("comments").child(postId).child(postId).child("yorum_tarih").setValue(ServerValue.TIMESTAMP)
            mRef.child("comments").child(postId).child(postId).child("yorum").setValue(etpostAciklama.text.toString())
            mRef.child("comments").child(postId).child(postId).child("yorum_begeni").setValue("0")
        }
        postSayisiniGuncelle()
        var intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)

    }

    private fun postSayisiniGuncelle() {
        mRef.child("users").child(mUser.uid).child("user_detail")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    var oankiGonderiSayisi = p0.child("post").getValue().toString().toInt()
                    oankiGonderiSayisi++
                    mRef.child("users").child(mUser.uid).child("user_detail").child("post")
                        .setValue(oankiGonderiSayisi.toString())

                }
            })
    }


    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onSecilenDosyaEvent(secilenDosya: EventbusDataEvents.PaylasilacakResmiGonder) {
        secilenDosyaYolu = secilenDosya.dosyaYolu!!
        dosyaTuruResimMi = secilenDosya.dosyaTuruResimMi
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    fun uploadStorage(filePath: String?) {
        var fileUri = Uri.parse("file://" + filePath)
        var ref = mStorageReference.child("users").child(mUser.uid).child(fileUri.lastPathSegment!!)
        var uploadTask = ref.putFile(fileUri);
        var dialogYukleniyor = YukleniyorFragment()

        dialogYukleniyor.show(activity!!.supportFragmentManager, "yukleniyorFragmenti")
        dialogYukleniyor.isCancelable = false
        uploadTask.addOnCompleteListener { p0 ->
            dialogYukleniyor.dismiss()
            if (p0!!.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                    override fun onSuccess(p0: Uri) {
                        Log.e("success*****", p0.toString())
                        veritabaninaBilgileriYaz(p0.toString())
                        dialogYukleniyor.dismiss()

                    }
                })
            }
        }.addOnFailureListener { p0: Exception ->
            dialogYukleniyor.dismiss()
            Log.e("******",p0.message!!)
            Toast.makeText(context, p0.message, Toast.LENGTH_LONG).show()

        }.addOnProgressListener { p0: UploadTask.TaskSnapshot ->
            var progress = 100 * p0.bytesTransferred / p0.totalByteCount
            dialogYukleniyor.tvBilgi.text = progress.toString()
        }
    }
}