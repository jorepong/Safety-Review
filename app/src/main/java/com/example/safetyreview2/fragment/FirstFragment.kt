package com.example.safetyreview2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safetyreview2.R
import com.example.safetyreview2.adapter.FirstFragmentAdapter
import com.example.safetyreview2.adapter.SavedDocumentRecyclerAdapter
import com.example.safetyreview2.data.Document
import com.example.safetyreview2.data.User
import com.example.safetyreview2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FirstFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        uid = auth.currentUser?.uid.toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        CoroutineScope(Dispatchers.IO).launch {
            var carbonCopyDocument = db.collection("users").document(uid).get().await().get("carbonCopyDocument") as ArrayList<String>
            var documentList = ArrayList<Document>()

            for (documentId in carbonCopyDocument) {
                db.collection("documents").document(documentId).get()
                    .addOnSuccessListener { documentSnapShot ->
                        var document = documentSnapShot.toObject<Document>()
                        if (document != null)
                            documentList.add(document)
                    }.await()
            }

            withContext(Dispatchers.Main) {
                view.findViewById<LinearLayout>(R.id.progressRing).visibility = View.GONE
                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                val adapter = FirstFragmentAdapter(documentList)
                recyclerView.layoutManager = LinearLayoutManager(view.context)
                recyclerView.adapter = adapter
                recyclerView.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager(view.context).orientation))
            }
        }

        return view
    }

    private suspend fun getDocumentList(): ArrayList<String> {
        var list = ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { data ->
                    if (data != null) {
                        if (data.get("confirmDocument") != null) {
                            list = data.get("confirmDocument") as ArrayList<String>
                            Log.d("DEBUG 3", "${list.size}")
                        }
                    }
                }.await()
        }

        Log.d("DEBUG 2", "${list.size}")
        return list
    }
}