package com.example.safetyreview2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.safetyreview2.R
import com.example.safetyreview2.databinding.ActivityReadDocumentBinding
import com.example.safetyreview2.databinding.ActivitySavedDocumentsBinding

class ReadDocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReadDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.exitBtn.setOnClickListener {
            finish()
        }
    }
}