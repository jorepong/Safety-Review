package com.example.safetyreview2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.safetyreview2.CheckBoxTriStates
import com.example.safetyreview2.OnIntentReceived
import com.example.safetyreview2.R
import com.example.safetyreview2.TouchHelperCallback
import com.example.safetyreview2.activity.CreateDocumentActivity
import com.example.safetyreview2.activity.ItemDetailActivity
import java.util.*

class CreateDocumentRecyclerAdapter(val context: Context, val activity: CreateDocumentActivity, private val items: ArrayList<String>):
    RecyclerView.Adapter<CreateDocumentRecyclerAdapter.ViewHolder>(),
    TouchHelperCallback.OnItemMoveListener, OnIntentReceived {

    private var enteredView: View? = null

    override fun onIntent(i: Intent?, resultCode: Int) {
        enteredView?.findViewById<CardView>(R.id.image_card)?.visibility = View.VISIBLE
        enteredView?.findViewById<ImageView>(R.id.image_view)?.setImageURI(i?.data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateDocumentRecyclerAdapter.ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ViewHolder(inflater.inflate(R.layout.item_article, parent, false))
    }

    override fun onBindViewHolder(holder: CreateDocumentRecyclerAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], position)
        holder.handle.setOnTouchListener{ _, event ->
            if(event.action == MotionEvent.ACTION_DOWN) {
                activity.touchHelper?.startDrag(holder)
            }
            return@setOnTouchListener true
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val handle: ImageView = view.findViewById(R.id.handle)
        fun bind(str: String, position: Int) {
            val checkBox = view.findViewById<CheckBoxTriStates>(R.id.checkBox)
            val textView = view.findViewById<EditText>(R.id.text_view)
            val detailSetting = view.findViewById<ImageView>(R.id.setting)

            detailSetting.setOnClickListener {
                val intent = Intent(activity.applicationContext, ItemDetailActivity::class.java)
                activity.startActivityForResult(intent, 100)
                enteredView = view
            }

            checkBox.setOnClickListener {
                val noteLayout = view.findViewById<CardView>(R.id.noteLayout)
                val note = view.findViewById<EditText>(R.id.note)

                if(checkBox.state == 0) {
                    noteLayout.visibility = View.GONE
                    checkBox.buttonTintList = context.resources.getColorStateList(R.color.grey)
                } else if(checkBox.state == 1) {
                    noteLayout.visibility = View.VISIBLE
                    note.isEnabled = true
                    checkBox.buttonTintList = context.resources.getColorStateList(R.color.red)
                    note.setBackgroundColor(Color.parseColor("#F6F6F6"))
                    note.setTextColor(Color.parseColor("#444444"))
                } else if (checkBox.state == 2) {
                    checkBox.buttonTintList = context.resources.getColorStateList(R.color.ios_blue)
                    if (note.text.toString().isBlank()) {
                        noteLayout.visibility = View.GONE
                    } else {
                        noteLayout.visibility = View.VISIBLE
                        note.isEnabled = false
                        note.setBackgroundColor(Color.parseColor("#FFFFFA"))
                        note.setTextColor(Color.parseColor("#5A5A5A"))
                    }
                }
            }

            textView.setOnFocusChangeListener { _, hasFocus ->
                if(position != (items.size - 1)) {
                    return@setOnFocusChangeListener
                }
                if(!hasFocus) {
                    if(textView.text.isNotBlank()) {
                        checkBox.buttonTintList = context.resources.getColorStateList(R.color.grey)
                        view.findViewById<ImageView>(R.id.handle).visibility = View.VISIBLE
                        view.findViewById<ImageView>(R.id.setting).visibility = View.VISIBLE
                        view.findViewById<View>(R.id.margin_view).visibility = View.GONE
                        view.findViewById<CheckBoxTriStates>(R.id.checkBox).isEnabled = true
                        items.add("test")
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if((toPosition + 1) == items.size)
            return
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}