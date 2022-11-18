package com.example.safetyreview2.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safetyreview2.data.Document
import com.example.safetyreview2.R
import java.text.SimpleDateFormat


class SavedDocumentRecyclerAdapter(private val list: ArrayList<Document>, private val activity: Activity):
    RecyclerView.Adapter<SavedDocumentRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ViewHolder(inflater.inflate(R.layout.item_saved_document, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(document: Document) {
            view.findViewById<TextView>(R.id.documentTitle).text = document.title
            view.findViewById<TextView>(R.id.createdTime).text = "최초 생성 · " + SimpleDateFormat("yyyy.MM.dd HH:mm").format(document.createdTime).toString()
            view.findViewById<TextView>(R.id.modifiedTime).text = "최종 수정 · " + SimpleDateFormat("yyyy.MM.dd HH:mm").format(document.modifiedTime).toString()

            if(document.createdTime?.time != document.modifiedTime?.time)
                view.findViewById<TextView>(R.id.modifiedTime).visibility = View.VISIBLE

            view.setOnClickListener {
                var intent = Intent(view.context, Document::class.java)
                intent.putExtra("documentData", document)
                activity.setResult(200, intent)
                activity.finish()
            }
        }
    }
}