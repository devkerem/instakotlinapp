package com.keremk.instakotlinapp.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.Models.Mesaj
import com.keremk.instakotlinapp.Models.Users
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.MesajRecyclerViewAdapter
import com.keremk.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    lateinit var sohbetEdilecekUserId: String
    lateinit var mesajGonderenUserId: String
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var sohbetEdilecekUser: Users? = null
    lateinit var mRef: DatabaseReference
    lateinit var myRecyclerView: RecyclerView
    lateinit var myRecyclerViewAdapter: MesajRecyclerViewAdapter
    val SAYFA_BASI_GONDERI_SAYISI = 5
    var sayfaNumarasi = 1
    lateinit var childEventListener: ChildEventListener
    lateinit var childEventListenerDahaFazla: ChildEventListener
    var tumMesajlar: ArrayList<Mesaj> = ArrayList()
    var mesajPos = 0
    var dahaFazlaMesajPos = 0
    var ilkGetirilenMesajID = ""
    var zatenListedeOlanMesajID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setupAuthListener()
        mRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        sohbetEdilecekUserId = intent.getStringExtra("secilenUserID")!!
        mesajGonderenUserId = mAuth.currentUser!!.uid
        sohbetEdenlerinBilgileriniGetir(sohbetEdilecekUserId, mesajGonderenUserId)
        refreshLayout.setOnRefreshListener {
            mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount > tumMesajlar.size) {
                        dahaFazlaMesajPos = 0
                        dahaFazlaMesajlariGetir()
                    }
                }
            })
            refreshLayout.setRefreshing(false)
            sayfaNumarasi++
            dahaFazlaMesajPos = 0
            //mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).removeEventListener(childEventListener)
            dahaFazlaMesajlariGetir()

        }
        Toast.makeText(applicationContext, sohbetEdilecekUserId, Toast.LENGTH_SHORT).show()
        tvMesajGonderButton.setOnClickListener {
            var currentTime = ServerValue.TIMESTAMP
            var mesajText = etMesaj.getText().toString()
            var mesajAtan = HashMap<String, Any>()
            mesajAtan.put("mesaj", mesajText)
            mesajAtan.put("goruldu", true)
            mesajAtan.put("time", currentTime)
            mesajAtan.put("type", "text")
            mesajAtan.put("user_id", mesajGonderenUserId)
            var yeniMesajKey = mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).push().key
            mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).child(yeniMesajKey!!).setValue(mesajAtan)

            var mesajAlan = HashMap<String, Any>()
            mesajAlan.put("mesaj", mesajText)
            mesajAlan.put("goruldu", false)
            mesajAlan.put("time", currentTime)
            mesajAlan.put("type", "text")
            mesajAlan.put("user_id", mesajGonderenUserId)
            mRef.child("mesajlar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).child(yeniMesajKey).setValue(mesajAlan)

            var konusmaMesajAtan = HashMap<String, Any>()
            konusmaMesajAtan.put("time", currentTime)
            konusmaMesajAtan.put("goruldu", true)
            konusmaMesajAtan.put("son_mesaj", mesajText)

            mRef.child("konusmalar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).setValue(konusmaMesajAtan)

            var konusmaMesajAlan = HashMap<String, Any>()
            konusmaMesajAlan.put("time", currentTime)
            konusmaMesajAlan.put("goruldu", false)
            konusmaMesajAlan.put("son_mesaj", mesajText)

            mRef.child("konusmalar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).setValue(konusmaMesajAlan)

            etMesaj.setText("")
        }
    }

    private fun dahaFazlaMesajlariGetir() {
        childEventListenerDahaFazla = mRef.child("mesajlar")
            .child(mesajGonderenUserId).child(sohbetEdilecekUserId)
            .orderByKey()
            .endAt(ilkGetirilenMesajID).limitToLast(SAYFA_BASI_GONDERI_SAYISI)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var okunanMesaj = snapshot.getValue(Mesaj::class.java)
                    if (!zatenListedeOlanMesajID.equals(snapshot.key)) {
                        tumMesajlar.add(dahaFazlaMesajPos++, okunanMesaj!!)
                    } else {
                        zatenListedeOlanMesajID = ilkGetirilenMesajID
                    }
                    if (dahaFazlaMesajPos == 1) {
                        ilkGetirilenMesajID = snapshot.key!!
                    }

                    myRecyclerViewAdapter.notifyDataSetChanged()
                    myRecyclerView.scrollToPosition(SAYFA_BASI_GONDERI_SAYISI)

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun mesajlariGetir() {

        childEventListener = mRef.child("mesajlar").child(mesajGonderenUserId)
            .child(sohbetEdilecekUserId).limitToLast(SAYFA_BASI_GONDERI_SAYISI).addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var okunanMesaj = snapshot.getValue(Mesaj::class.java)
                    tumMesajlar.add(okunanMesaj!!)
                    if (mesajPos == 0) {
                        ilkGetirilenMesajID = snapshot.key!!
                        zatenListedeOlanMesajID = snapshot.key!!
                    }
                    mesajPos++
                    myRecyclerViewAdapter.notifyItemInserted(tumMesajlar.size - 1)
                    myRecyclerView.scrollToPosition(tumMesajlar.size - 1)

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
            })
    }

    private fun setupMesajlarRecyclerView() {
        var myLinearLayout = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        myLinearLayout.stackFromEnd = true
        myRecyclerView = rvSohbet


        myRecyclerViewAdapter = MesajRecyclerViewAdapter(tumMesajlar, applicationContext, sohbetEdilecekUser!!)
        myRecyclerView.layoutManager = myLinearLayout
        myRecyclerView.adapter = myRecyclerViewAdapter
        mesajlariGetir()

    }

    private fun sohbetEdenlerinBilgileriniGetir(sohbetEdilecekUserId: String, oturumAcanUserID: String) {
        mRef.child("users").child(sohbetEdilecekUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue() != null) {
                    sohbetEdilecekUser = snapshot.getValue(Users::class.java)
                    sohbetEdilecekUserName.setText(sohbetEdilecekUser!!.user_name)
                    setupMesajlarRecyclerView()

                }
            }
        })
        mRef.child("users").child(oturumAcanUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue() != null) {
                    var bulunanKullanici = snapshot.getValue(Users::class.java)
                    var imgUrl = bulunanKullanici!!.user_detail!!.profile_picture.toString()
                    UniversalImageLoader.setImage(imgUrl, circleImageView, null, "")
                }
            }
        })
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            var user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                Log.e("HATA", "Kullanıcı oturum açmamış, HomeActivitydesn")
                var intent = Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {


            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("HATA", "HomeActivitydesin")
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}