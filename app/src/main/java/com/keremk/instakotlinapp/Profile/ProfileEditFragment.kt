package com.keremk.instakotlinapp.Profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.keremk.instakotlinapp.Models.Users
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.EventbusDataEvents
import com.keremk.instakotlinapp.utils.UniversalImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProfileEditFragment : Fragment() {
    lateinit var circleProfileImageFragment: CircleImageView
    var gelenKullaniciBilgileri: Users = Users("", "", "", "", "", "", "", null)
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mStorageRef: StorageReference

    var profilPhotoUri: Uri? = null

    val RESIM_SEC = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mStorageRef = FirebaseStorage.getInstance().reference

        setupKullaniciBilgileri(view)

        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }
        view.tvFotografiDegistir.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, RESIM_SEC)
        }

        view.imgBtnDegisiklikleriKaydet.setOnClickListener {

            var profilGuncellendiMi = false

            if (!gelenKullaniciBilgileri!!.adi_soyadi!!.equals(
                    view.etProfileName.text.toString()
                )
            ) {
                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id.toString()).child("adi_soyadi").setValue(view.etProfileName.text.toString())
                profilGuncellendiMi = true
            }

            if (!gelenKullaniciBilgileri!!.user_detail!!.biography!!.equals(
                    view.etUserBio.text.toString()
                )
            ) {
                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id.toString()).child("user_detail").child("biography").setValue(view.etUserBio.text.toString())
                profilGuncellendiMi = true
            }

            if (!gelenKullaniciBilgileri!!.user_detail!!.web_site!!.equals(
                    view.etUserWebSite.text.toString()
                )
            ) {
                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id.toString()).child("user_detail").child("web_site").setValue(view.etUserWebSite.text.toString())
                profilGuncellendiMi = true
            }

            if (!gelenKullaniciBilgileri!!.user_name!!.equals(view.etUserName.text.toString())) {
                mDatabaseRef.child("users").orderByChild("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var userNameKullanimdaMi = false
                            for (ds in p0!!.children) {
                                var okunanKullaniciAdi = ds!!.getValue(Users::class.java)!!.user_name
                                if (okunanKullaniciAdi!!.equals(view.etUserName.text.toString())) {
                                    Toast.makeText(
                                        activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT
                                    ).show()
                                    userNameKullanimdaMi = true
                                    break
                                }
                            }

                            if (userNameKullanimdaMi == false) {
                                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id.toString()).child("user_name").setValue(view.etUserName.text.toString())
                                profilGuncellendiMi = true
                            }
                        }
                    })
            }

            Toast.makeText(activity, "Kullanıcı güncellendi", Toast.LENGTH_SHORT).show()
            if (profilPhotoUri != null) {

                var dialogYukleniyor = YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager, "yukleniyorFragmenti")
                var storageReference = mStorageRef.child("users").child(gelenKullaniciBilgileri!!.user_id.toString()!!).child(profilPhotoUri!!.lastPathSegment.toString())
                var file = storageReference.putFile(profilPhotoUri!!);
                file.addOnCompleteListener { p0 -> //                            val downloadUrl: Uri = p0.result.
                    if (p0!!.isSuccessful) {

                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG)
                            Log.e("///////*/*", uri.toString())
                            Log.e("******---", "geldiiiiii")
                            Log.e("******", p0.result.toString())
                            Toast.makeText(
                                activity, "Resim yüklendi" + p0!!.getResult()?.storage?.downloadUrl.toString(), Toast.LENGTH_LONG
                            ).show()

                            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id.toString()).child("user_detail").child("profile_picture").setValue(uri.toString())
                            println(uri.toString())
                            profilGuncellendiMi = true

                            dialogYukleniyor.dismiss()
                        }
                    }
                }.addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            Log.e("HATA", p0!!.message.toString())
                        }
                    });
            }

            if (profilGuncellendiMi == true) {
                Toast.makeText(activity, "Kullanıcı güncellendi", Toast.LENGTH_SHORT).show()
            }


        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESIM_SEC && resultCode == AppCompatActivity.RESULT_OK && data!!.data != null) {

            var profilResimURI = data.data
            profilPhotoUri = data.data!!

            circleProfileImage.setImageURI(profilResimURI)
            circleProfileImage.setImageURI(profilPhotoUri)
        }
    }

    private fun setupKullaniciBilgileri(view: View?) {
        view!!.etProfileName.setText(gelenKullaniciBilgileri!!.adi_soyadi)
        view!!.etUserName.setText(gelenKullaniciBilgileri!!.user_name)
        if (!gelenKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()) {
            view!!.etUserBio.setText(gelenKullaniciBilgileri!!.user_detail!!.biography)
        }
        if (!gelenKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()) {
            view!!.etUserWebSite.setText(gelenKullaniciBilgileri!!.user_detail!!.web_site)
        }
        var imgUrl = gelenKullaniciBilgileri!!.user_detail!!.profile_picture
        UniversalImageLoader.setImage(imgUrl!!, view!!.circleProfileImage, view!!.progressBar, "")
    }

    //////////////////////////// EVENTBUS /////////////////////////////////
    @Subscribe(sticky = true) internal fun onKullaniciBilgileriEvent(
        kullaniciBilgileri: EventbusDataEvents.KullaniciBilgileriniGonder
    ) {
        gelenKullaniciBilgileri = kullaniciBilgileri!!.kullanici!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
}