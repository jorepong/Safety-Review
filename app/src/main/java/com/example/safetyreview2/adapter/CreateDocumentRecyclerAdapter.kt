package com.example.safetyreview2.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
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
import com.example.safetyreview2.data.ChecklistItem
import java.util.*

class CreateDocumentRecyclerAdapter(val activity: CreateDocumentActivity, private val items: ArrayList<ChecklistItem>):
    RecyclerView.Adapter<CreateDocumentRecyclerAdapter.ViewHolder>(),
    TouchHelperCallback.OnItemMoveListener, OnIntentReceived {

    private var selectedView: View? = null
    private var selectedViewPosition: Int = -1
    private var context = activity.applicationContext

    override fun onIntent(i: Intent?, resultCode: Int) {
        if(i?.data != null || i?.getStringExtra("imageURL") != null) {
            Log.d("debug", "1")
            selectedView?.findViewById<CardView>(R.id.image_cardview)?.visibility = View.VISIBLE
            selectedView?.findViewById<ImageView>(R.id.imageView)?.setImageURI(i?.data)
            items[selectedViewPosition].imageURL = i?.data.toString()
        } else {
            Log.d("debug", "2")
            selectedView?.findViewById<CardView>(R.id.image_cardview)?.visibility = View.GONE
            items[selectedViewPosition].imageURL = ""
        }
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

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val checkBox: CheckBoxTriStates = view.findViewById(R.id.checkBox)
        private val textView: EditText = view.findViewById(R.id.text_view)
        private val settingImageView: ImageView = view.findViewById(R.id.setting)
        private val noteLayout: CardView = view.findViewById(R.id.noteLayout)
        private val note: EditText = view.findViewById(R.id.note)
        val handle: ImageView = view.findViewById(R.id.handle)

        fun bind(item: ChecklistItem, position: Int) {
            settingImageView.setOnClickListener {
                val intent = Intent(activity.applicationContext, ItemDetailActivity::class.java)
                if(item.imageURL.isNotBlank()) {
                    intent.putExtra("imageURL", item.imageURL)
                }
                activity.startActivityForResult(intent, 100)
                selectedView = view
                selectedViewPosition = position
            }

            checkBox.setOnClickListener {
                when (checkBox.state) {
                    0 -> {
                        noteLayout.visibility = View.GONE
                        checkBox.buttonTintList = context.resources.getColorStateList(R.color.grey)
                        item.checkState = 0
                    }
                    1 -> {
                        noteLayout.visibility = View.VISIBLE
                        note.isEnabled = true
                        checkBox.buttonTintList = context.resources.getColorStateList(R.color.red)
                        note.setBackgroundColor(Color.parseColor("#F6F6F6"))
                        note.setTextColor(Color.parseColor("#444444"))
                        item.checkState = 1
                    }
                    2 -> {
                        checkBox.buttonTintList = context.resources.getColorStateList(R.color.red)
                        if (note.text.toString().isBlank()) {
                            noteLayout.visibility = View.GONE
                        } else {
                            noteLayout.visibility = View.VISIBLE
                            note.isEnabled = false
                            note.setBackgroundColor(Color.parseColor("#FFFFFA"))
                            note.setTextColor(Color.parseColor("#5A5A5A"))
                        }
                        item.checkState = 2
                    }
                }
            }

            textView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(s!!.isBlank())
                        item.textBoxText = "빈 항목"
                    else
                        item.textBoxText = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

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
                        items.add(ChecklistItem())
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
//        if((toPosition + 1) == items.size)
//            return
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}