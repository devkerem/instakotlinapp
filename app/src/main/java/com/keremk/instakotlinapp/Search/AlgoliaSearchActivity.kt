package com.keremk.instakotlinapp.Search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.helpers.InstantSearch
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.instantsearch.utils.ItemClickSupport
import com.google.firebase.auth.FirebaseAuth
import com.keremk.instakotlinapp.Generic.UserProfileActivity
import com.keremk.instakotlinapp.Login.LoginActivity
import com.keremk.instakotlinapp.Profile.ProfileActivity
import com.keremk.instakotlinapp.R
import com.keremk.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_algolia_search.*

//import kotlinx.android.synthetic.main.activity_algolia_search.*

class AlgoliaSearchActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 1
    private val TAG = "Search Activity"
    private val ALGOLIA_APP_ID = "ZNOH90NFYM"
    private val ALGOLIA_API_KEY = "72dde6af73ac74d040c19b7af534bd7c"
    private val ALGOLIA_INDEX_NAME = "KotlinInstagram"
    lateinit var searcher: Searcher
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_algolia_search)
        mAuth = FirebaseAuth.getInstance()
        setupAuthListener()
        setupNavigationView()
        setupAlgoliaSearch()
        listeAramaSonuclari.setOnItemClickListener { recyclerView:RecyclerView, position:Int, v:View ->
            var secilenUserId =listeAramaSonuclari.get(position).getString("user_id")
            if (secilenUserId.equals(mAuth.currentUser!!.uid)){
             var intent = Intent(applicationContext,ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)

            }else{
                var intent = Intent(applicationContext,UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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

    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx)
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationViewEx)
        var menu = bottomNavigationViewEx.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
        Log.e(TAG, "setupNavigationView: " )
    }
    private fun setupAuthListener() {
        mAuthListener= FirebaseAuth.AuthStateListener {
            var user=FirebaseAuth.getInstance().currentUser

            if(user == null){
                Log.e("HATA","Kullanıcı oturum açmamış, HomeActivitydesn")
                var intent= Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }else {
                var intent = Intent(applicationContext,UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
    }

}