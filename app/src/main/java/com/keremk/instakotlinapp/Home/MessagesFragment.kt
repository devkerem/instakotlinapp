package com.keremk.instakotlinapp.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.Models.Konusmalar
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.KonusmalarRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_messages.view.*

class MessagesFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var tumKonusmalar: ArrayList<Konusmalar> = ArrayList()
    lateinit var myRecyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var myAdapter: KonusmalarRecyclerAdapter
    lateinit var myFragmentViewHolder: View
    lateinit var mRef: DatabaseReference
    var listenerAtandimi: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myFragmentViewHolder = inflater.inflate(R.layout.fragment_messages, container, false)
        mAuth = FirebaseAuth.getInstance()
        setupAuthListener()
        myFragmentViewHolder.searchView.setOnClickListener {
            var intent = Intent(activity, AlgoliaSearchMesajActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        setupKonusmalarRecyclerView()

        return myFragmentViewHolder
    }

    private fun tumKonusmalariGetir() {
        mRef = FirebaseDatabase.getInstance().reference.child("konusmalar").child(mAuth.currentUser!!.uid)
        if (!listenerAtandimi){
            listenerAtandimi =true
            mRef.orderByChild("time").addChildEventListener(myListener)
        }
    }

    private var myListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            var kontrol =konusmaPositionBul(snapshot.key.toString())
            if (kontrol!= -1){
                var guncellenecekKonusma = snapshot.getValue(Konusmalar::class.java)
                guncellenecekKonusma!!.user_id= snapshot.key
                tumKonusmalar.removeAt(kontrol)
                myAdapter.notifyItemRemoved(kontrol)
                tumKonusmalar.add(0,guncellenecekKonusma)
                myAdapter.notifyItemInserted(0)
            }
        }
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            var eklenecekKonusma = snapshot.getValue(Konusmalar::class.java)
            eklenecekKonusma!!.user_id=snapshot.key
            tumKonusmalar.add(0,eklenecekKonusma)
            myAdapter.notifyItemInserted(0)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}
    }

    private fun konusmaPositionBul(userID:String):Int{
        for (i in 0..tumKonusmalar.size-1){
            var gecici = tumKonusmalar[i]
            if (gecici.user_id.equals(userID)){
                return i
            }
        }
        return -1
    }

    private fun setupKonusmalarRecyclerView() {
        myRecyclerView = myFragmentViewHolder.recyclerKonusmalar
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        myAdapter = KonusmalarRecyclerAdapter(tumKonusmalar, activity!!)
        myRecyclerView.layoutManager = linearLayoutManager
        myRecyclerView.adapter = myAdapter
        tumKonusmalariGetir()
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            var user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.e("HATA", "Kullanıcı oturum açmamış, ProfileActivitydesin")
                var intent = Intent(context, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity!!.finish()
            } else {
            }
        }
    }

    override fun onPause() {
        super.onPause()
        tumKonusmalar.clear()
        if (listenerAtandimi){
            listenerAtandimi =false
            mRef.removeEventListener(myListener)
        }
    }

    override fun onResume() {
        super.onResume()
        tumKonusmalar.clear()
        if (!listenerAtandimi){
            listenerAtandimi=true
            myAdapter.notifyDataSetChanged()
            mRef.orderByChild("time").addChildEventListener(myListener)
        }
    }
}