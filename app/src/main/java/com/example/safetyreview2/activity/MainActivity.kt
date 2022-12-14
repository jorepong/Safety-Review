package com.example.safetyreview2.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.safetyreview2.R
import com.example.safetyreview2.adapter.FragmentAdapter
import com.example.safetyreview2.data.Document
import com.example.safetyreview2.data.User
import com.example.safetyreview2.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        var uid = auth.currentUser?.uid.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val userDataSnapShot = db.collection("users").document(uid).get().await()
            val carbonCopyDocument = userDataSnapShot.get("carbonCopyDocument") as ArrayList<String>
            val user = userDataSnapShot.toObject<User>()

            withContext(Dispatchers.Main) {
                if(user != null) {
                    binding.name.text = "${user.rank} ${user.name}"
                    binding.position.text = "${user.position}"
                }
                if(carbonCopyDocument.size != 0) {
                    binding.numberOfUnreadDocument.text = carbonCopyDocument.size.toString()
                    binding.numberOfUnreadDocument.setTextColor(Color.parseColor("#FF5900"))
                }
                binding.progressRing.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "???????????? ??????", Toast.LENGTH_SHORT).show()
        }

        binding.checkUpdate.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jorepong/Safety-Review/releases/download/0.1.0/safety_review.apk"))
            startActivity(intent)
//            val intent = Intent(applicationContext, UpdatePageActivity::class.java)
//            startActivity(intent)
        }
//
//        binding.btnDownload.setOnClickListener {
//            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jorepong/Safety-Review/releases/download/0.1.0/safety_review.apk"))
//            startActivity(intent)
//        }

        binding.newDocument.setOnClickListener {
            val intent = Intent(applicationContext, CreateDocumentActivity::class.java)
            startActivity(intent)
        }

        binding.viewPager.adapter = FragmentAdapter(supportFragmentManager, lifecycle)

        val tabTitles = listOf<String>("?????? ??????", "?????? ??????")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val unreadDocumentLayout = binding.unreadDocuments
        unreadDocumentLayout.setOnClickListener {
            if(unreadDocumentLayout.tag == "0") {
                binding.fragmentOfDocument.visibility = View.VISIBLE
                binding.dropdownUnreadDocuments.setImageResource(R.drawable.ic_round_keyboard_arrow_up_24)
                unreadDocumentLayout.tag = "1"
            } else {
                binding.fragmentOfDocument.visibility = View.GONE
                binding.dropdownUnreadDocuments.setImageResource(R.drawable.ic_round_keyboard_arrow_down_24)
                unreadDocumentLayout.tag = "0"
            }
        }
    }
}