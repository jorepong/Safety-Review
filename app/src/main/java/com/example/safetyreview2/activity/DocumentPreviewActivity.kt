package com.example.safetyreview2.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import com.example.safetyreview2.R
import com.example.safetyreview2.data.Document
import com.example.safetyreview2.databinding.ActivityDocumentPreviewBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DocumentPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentPreviewBinding
    private lateinit var document: Document
    private val database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        document = intent.getSerializableExtra("data") as Document
        applyData()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            send()
        }
    }

    private fun send() {
        var canSendDocument = true

        if(document.confirmer!!.size == 0) {
            canSendDocument = false
            AlertDialog.Builder(this)
                .setMessage("최소 한명의 승인자를 지정해야 합니다.")
                .setPositiveButton("확인", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                    }
                })
                .create()
                .show()
            return
        }

        for(item in document.carbonCopy!!) {
            val uid = item.uid.toString()
            database.collection("users").document(uid).update("carbonCopyDocument", FieldValue.arrayUnion(document.documentId))
        }

        writeDocumentData()
        Toast.makeText(this, "문서를 전송했습니다.", Toast.LENGTH_SHORT).show()
        setResult(300)
        finish()
    }

    private fun applyData() {
        val carbonCopyLayout = binding.carbonCopyLayout

        // 참조자 및 승인자 데이터 적용
        for (user in document.carbonCopy!!) {
            val textView = layoutInflater.inflate(R.layout.item_recipient_box, carbonCopyLayout, false)
            carbonCopyLayout.addView(textView)
            textView.findViewById<TextView>(R.id.name).text = user.name.toString()
        }

        binding.numberOfItem.text = (document.checklist!!.size - 1).toString()
        binding.title.text = document.title.toString()

        // 체크리스트 데이터 적용
        for (item in document.checklist!!) {
            if(item.textBoxText.isBlank())
                continue
            val checklistItem = layoutInflater.inflate(R.layout.item_checklist, binding.checklistContainer, false)
            binding.checklistContainer.addView(checklistItem)
            checklistItem.findViewById<TextView>(R.id.text).text = item.textBoxText

            if(item.imageURL.isNotBlank()) {
                checklistItem.findViewById<ImageView>(R.id.imageView).setImageURI(item.imageURL.toUri())
                checklistItem.findViewById<CardView>(R.id.image_cardview)?.visibility = View.VISIBLE
            }
        }
    }

    fun writeDocumentData() {
        val documentReference = database.collection("documents").document(document.documentId.toString())
        documentReference.get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                // 문서가 존재할 경우 ▶ 내용 업데이트
                if(task.result.exists()) {
                    documentReference.update(
                        hashMapOf(
                            "title" to document.title,
                            "modifiedTime" to FieldValue.serverTimestamp(),
                            "carbonCopy" to document.carbonCopy,
                            "confirmer" to document.confirmer
                        )
                    )
                } else { // 문서가 존재하지 않는 경우 ▶ 새 문서 작성
                    documentReference.set(document)
                }
            }
        }
    }
}