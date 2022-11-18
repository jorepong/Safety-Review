//package com.example.safetyreview2.adapter
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.cardview.widget.CardView
//import androidx.core.app.ActivityCompat.startActivityForResult
//import androidx.recyclerview.widget.RecyclerView
//import com.example.safetyreview2.*
//import com.example.safetyreview2.itemData.Item
//import com.example.safetyreview2.itemData.ItemImage
//import com.example.safetyreview2.itemData.ItemText
//import java.util.*
//
//
//class ItemRecyclerAdapter(private val items: ArrayList<Item>):
//    RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>(),
//    TouchHelperCallback.OnItemMoveListener {
//
//    interface CallbackInterface {
//        fun onHandleSelection(position: Int, text: String?)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        if(viewType == 0) {
//            return ViewHolder(
//                inflater.inflate(R.layout.item_text, parent, false)
//            )
//        }
//        else {
//            return ViewHolder(
//                inflater.inflate(R.layout.item_image, parent, false)
//            )
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        if(items[position] is ItemText) return 0
//        else return 1
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(items[position])
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
//        fun bind(content: Item) {
//            val activity: Activity? = null
//            if(content is ItemImage) {
////                val imageUploadButton = view.findViewById<CardView>(R.id.image_upload)
////                imageUploadButton.setOnClickListener {
////                    val intent = Intent(view.context, ImageSelectActivity::class.java)
////                    startActivityForResult(view.context as Activity, intent, 100, null)
////                    startActivity(view.context, intent, null)
////                    val imageData = intent.getSerializableExtra("data") as Intent
////                    view.findViewById<ImageView>(R.id.image_view).setImageURI(imageData.data)
////                    startActivityForResult(view.context as Activity, Intent(view.context, ImageSelectActivity::class.java), 100 ,null)
////                    activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
////                    val mGetContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
//                    val intent = Intent(Intent.ACTION_PICK)
//                    intent.type = "image/*"
//                    startActivityForResult(view.context as Activity, intent, 100, null)
//                }
//            }
//        }
//
////        protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////////            super.onActivityResult(requestCode, resultCode, data)
////
////            if(requestCode == 100 && resultCode == Activity.RESULT_OK) {
////                Toast.makeText(view.context as Activity, "test", Toast.LENGTH_SHORT).show()
//////                view.findViewById<ImageView>(R.id.image_view).setImageURI(data?.data)
////            }
////        }
//    }
////
////    inner class GetData(): AppCompatActivity() {
////        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////            super.onActivityResult(requestCode, resultCode, data)
////
////            if(requestCode == 100 && resultCode == Activity.RESULT_OK) {
////                Toast.makeText(applicationContext, "test", Toast.LENGTH_SHORT).show()
//////                view.findViewById<ImageView>(R.id.image_view).setImageURI(data?.data)
////            }
////        }
////    }
////
////    override fun onItemMove(fromPosition: Int, toPosition: Int) {
////        Collections.swap(items, fromPosition, toPosition)
////        notifyItemMoved(fromPosition, toPosition)
////    }
//}