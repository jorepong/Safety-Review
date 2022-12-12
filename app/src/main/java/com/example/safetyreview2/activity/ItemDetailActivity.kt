package com.example.safetyreview2.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.children
import com.example.safetyreview2.OnIntentReceived
import com.example.safetyreview2.R
import com.example.safetyreview2.databinding.ActivityItemDetailBinding
import com.example.safetyreview2.databinding.ActivityLoginBinding

class ItemDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemDetailBinding
    var mIntentListener: OnIntentReceived? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finish.setOnClickListener {
            finish()
        }

        binding.addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 200)
        }

        if(intent.hasExtra("imageURL")) {
            val imageContainer = binding.imageContainer
            var addImageButton = binding.addImageButton
            val imageURL = intent.getStringExtra("imageURL")
            val imageItem = layoutInflater.inflate(R.layout.item_image_item, imageContainer, false)
            imageContainer.addView(imageItem)
            addImageButton.isEnabled = false
            addImageButton.setTextColor(resources.getColor(R.color.gray))
            imageItem.findViewById<ImageView>(R.id.image_src).setImageURI(imageURL!!.toUri())
            imageItem.findViewById<ImageView>(R.id.removeImageButton).setOnClickListener {
                imageContainer.removeView(imageItem)
                addImageButton.isEnabled = true
                addImageButton.setTextColor(resources.getColor(R.color.ios_blue))
            }
//            setResult(100, Intent().putExtra("imageURL", imageURL))
            setResult(100, Intent().setData(imageURL.toUri()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("debug", "onActivityResult")
        val imageContainer = binding.imageContainer
        var addImageButton = binding.addImageButton

        if(data?.data != null) {
            val imageItem = layoutInflater.inflate(R.layout.item_image_item, imageContainer, false)
            imageContainer.addView(imageItem)
            addImageButton.isEnabled = false
            addImageButton.setTextColor(resources.getColor(R.color.gray))
            imageItem.findViewById<ImageView>(R.id.image_src).setImageURI(data?.data)
            imageItem.findViewById<ImageView>(R.id.removeImageButton).setOnClickListener {
                imageContainer.removeView(imageItem)
                addImageButton.isEnabled = true
                addImageButton.setTextColor(resources.getColor(R.color.ios_blue))
            }
        }

        if (imageContainer.children == null) {
            setResult(100, null)
        } else {
            setResult(100, data)
        }
    }
}