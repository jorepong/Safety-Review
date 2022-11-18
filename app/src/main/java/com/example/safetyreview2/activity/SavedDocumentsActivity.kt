package com.example.safetyreview2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safetyreview2.data.Document
import com.example.safetyreview2.R
import com.example.safetyreview2.adapter.SavedDocumentRecyclerAdapter
import com.example.safetyreview2.databinding.ActivitySavedDocumentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SavedDocumentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedDocumentsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var documentList: ArrayList<Document>
    private val database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.exitBtn.setOnClickListener { finish() }
        CoroutineScope(Dispatchers.Main).launch {
            documentList = CoroutineScope(Dispatchers.IO).async {
                getDocumentList(auth.currentUser?.uid.toString())
            }.await()

            binding.progressBar.visibility = View.GONE
            initializeViews()
        }
    }

    private suspend fun getDocumentList(uid: String): ArrayList<Document> {
        var list = ArrayList<Document>()
        database.collection("documents").get().addOnSuccessListener { dataSnapShot ->
            if(dataSnapShot != null) {
                for(data in dataSnapShot) {
                    val document = Document()
                    val title = data.get("title").toString()
                    val createdTime = data.getTimestamp("createdTime")?.toDate()
                    val modifiedTime = data.getTimestamp("modifiedTime")?.toDate()
                    val documentId = data.get("documentId").toString()
                    document.title = title
                    document.createdTime = createdTime
                    document.modifiedTime = modifiedTime
                    document.documentId = documentId
                    list.add(document)
                }
            }
        }.await()
        return list
    }

    private fun initializeViews() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = SavedDocumentRecyclerAdapter(documentList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager(this).orientation))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        setResult(200, data)
    }
}