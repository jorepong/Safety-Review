package com.example.safetyreview2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.safetyreview2.R
import com.example.safetyreview2.activity.DocumentPreviewActivity
import com.example.safetyreview2.data.Document
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirstFragmentAdapter(private val list: ArrayList<Document>):
    RecyclerView.Adapter<FirstFragmentAdapter.ViewHolder>() {
    val db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirstFragmentAdapter.ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ViewHolder(inflater.inflate(R.layout.item_first_fragment, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(doc: Document) {
            view.findViewById<TextView>(R.id.classify).text = "수신"
            view.findViewById<TextView>(R.id.title).text = doc.title
            view.findViewById<CardView>(R.id.reading).setOnClickListener {
                val intent = Intent(view.context, DocumentPreviewActivity::class.java)
                view.context.startActivity(intent)
            }
        }
    }
}