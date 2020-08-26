package com.keremk.instakotlinapp.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.google.firebase.auth.FirebaseAuth
import com.keremk.instakotlinapp.Generic.UserProfileActivity
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.R
import kotlinx.android.synthetic.main.activity_algolia_search_mesaj.*

class AlgoliaSearchMesajActivity : AppCompatActivity() {
    private val ALGOLIA_APP_ID = "ZNOH90NFYM"
    private val ALGOLIA_API_KEY = "72dde6af73ac74d040c19b7af534bd7c"
    private val ALGOLIA_INDEX_NAME = "KotlinInstagram"
    private val TAG = "Algolia Search Mesaj"
    private val ACTIVITY_NO = 0

    lateinit var searcher: Searcher
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_algolia_search_mesaj)

        mAuth = FirebaseAuth.getInstance()
        setupAuthListener()
        setupAlgoliaSearch()
        listeAramaSonuclari.setOnItemClickListener { recyclerView: RecyclerView, position: Int, v: View ->
            var secilenUserId = listeAramaSonuclari.get(position).getString("user_id")
            if (!secilenUserId.equals(mAuth.currentUser!!.uid)) {
                var intent = Intent(applicationContext,ChatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("secilenUserID",secilenUserId)
                startActivity(intent)
            }
        }
    }

    private fun setupAlgoliaSearch() {
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_API_KEY, ALGOLIA_INDEX_NAME)
        var helper = InstantSearch(this, searcher)
        helper.search()
    }

    override fun onDestroy() {
        super.onDestroy()
        searcher.destroy()
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
                var intent = Intent(applicationContext, UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
    }
}