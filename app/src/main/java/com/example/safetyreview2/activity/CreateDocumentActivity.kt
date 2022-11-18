package com.example.safetyreview2.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safetyreview2.*
import com.example.safetyreview2.adapter.CreateDocumentRecyclerAdapter
import com.example.safetyreview2.adapter.CustomArrayAdapter
import com.example.safetyreview2.data.Document
import com.example.safetyreview2.data.User
import com.example.safetyreview2.databinding.ActivityCreateDocumentBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.time.LocalDateTime
import java.util.Date


class CreateDocumentActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCreateDocumentBinding
    private lateinit var documentRef: DocumentReference
    val db = Firebase.firestore
    var touchHelper: ItemTouchHelper? = null
    var mIntentListener: OnIntentReceived? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val document = db.collection("documents").document()
        documentRef = document

        val list = ArrayList<String>()
        list.add("")
        val recyclerView = binding.recyclerView
        val adapter = CreateDocumentRecyclerAdapter(this,this, list)
        adapter.setHasStableIds(true)
        mIntentListener = adapter
        val callback = TouchHelperCallback(adapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper?.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val slidePanel = binding.slidingUpPanelLayout

        binding.showPanel.setOnClickListener {
            val state = slidePanel.panelState
            // 닫힌 상태일 경우 열기
            if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }
        }

        binding.slidingUpPanelLayout.setFadeOnClickListener {
            binding.slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

        binding.closePanel.setOnClickListener {
            binding.slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

        val carbonCopyChipGroup = binding.carbonCopyChipGroup
        val recipientChipGroup = binding.recipientChipGroup
        val carbonCopyList = ArrayList<User>()
        val confirmerList = ArrayList<User>()

        // 자동 완성된 텍스트 선택 ▶ 칩으로 추가
        binding.carbonCopy.setOnItemClickListener { parent, _, position, _ ->
//            if(carbonCopyChipGroup.isEmpty()) {
//                binding.carbonCopy.hint = null
//            }
            val item = parent?.getItemAtPosition(position) as User
            carbonCopyList.add(item)
            val text = "${item.rank} ${item.name}"
            val chip = Chip(this@CreateDocumentActivity)
            chip.text = text
            chip.setChipIconResource(R.drawable.ic_baseline_person_24)
            chip.isCloseIconVisible = true
            carbonCopyChipGroup.addView(chip as View)
            chip.setOnCloseIconClickListener {
                carbonCopyChipGroup.removeView(chip as View)
            }
            binding.carbonCopy.text = null
        }

        // 자동 완성된 텍스트 선택 ▶ 칩으로 추가
        binding.recipient.setOnItemClickListener { parent, _, position, _ ->
            val item = parent?.getItemAtPosition(position) as User
            confirmerList.add(item)
            val text = "${item.rank} ${item.name}"
            val chip = Chip(this@CreateDocumentActivity)
            chip.text = text
            chip.setChipIconResource(R.drawable.ic_baseline_person_24)
            chip.isCloseIconVisible = true
            recipientChipGroup.addView(chip as View)
            chip.setOnCloseIconClickListener { recipientChipGroup.removeView(chip as View) }
            binding.recipient.text = null
        }

        val userList = loadUsers()

        binding.carbonCopy.setAdapter(CustomArrayAdapter(this, R.layout.dropdown_article, userList))
        binding.recipient.setAdapter(CustomArrayAdapter(this, R.layout.dropdown_article, userList))

        binding.save.setOnClickListener { save() }
        binding.load.setOnClickListener { load() }
        binding.exit.setOnClickListener { finish() }
        binding.preview.setOnClickListener { preview(carbonCopyList, confirmerList) }
    }

    private fun preview(carbonCopyList: ArrayList<User>, confirmerList: ArrayList<User>) {
        for(item in carbonCopyList) {
            val uid = item.uid.toString()
            val document = db.collection("users").document(uid)
            document.update("carbonCopyDocument", FieldValue.arrayUnion(documentRef?.id))
        }

        db.collection("users").get().addOnSuccessListener { dataSnapShot ->
            if (dataSnapShot != null) {
                for(data in dataSnapShot) {
//                    val rank = data.get("rank").toString()
//                    val name = data.get("name").toString()
//                    val str = "$rank $name"
                }
            }
        }
    }

    private fun loadUsers(): ArrayList<User> {
        val list = ArrayList<User>()
        db.collection("users").get().addOnSuccessListener { dataSnapShot ->
            if(dataSnapShot != null) {
                for(data in dataSnapShot) {
                    val email = data.get("email").toString()
                    val rank = data.get("rank").toString()
                    val name = data.get("name").toString()
                    val position = data.get("position").toString()
                    val uid = data.id

                    val user = User(email, rank, name, position, uid)
                    if(uid != auth.currentUser?.uid.toString())
                        list.add(user)
                }
            }
        }
        return list
    }

    private fun save() {
        var titleText = binding.title.text.toString()
        val descriptionText = binding.description.text
        val carbonCopyList = ArrayList<String>()
        val confirmerList = ArrayList<String>()

        if(titleText.isBlank())
            titleText = "제목 없는 문서"

        for(chip in binding.carbonCopyChipGroup.children) carbonCopyList.add((chip as Chip).text.toString())
        for(chip in binding.recipientChipGroup.children) confirmerList.add((chip as Chip).text.toString())

        documentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    documentRef.update(
                        hashMapOf(
                        "title" to titleText,
                        "modifiedTime" to FieldValue.serverTimestamp(),
                        "carbonCopy" to carbonCopyList,
                        "confirmer" to confirmerList
                        )
                    )
                    Toast.makeText(this, "저장된 문서를 수정했습니다.", Toast.LENGTH_SHORT).show()

                } else {
                    val document = Document(titleText, Date(), Date(), documentRef!!.id, carbonCopyList, confirmerList)
                    documentRef.set(document)
//                    documentRef.set(
//                        hashMapOf(
//                            "title" to titleText,
//                            "createdTime" to FieldValue.serverTimestamp(),
//                            "modifiedTime" to FieldValue.serverTimestamp(),
//                            "documentId" to documentRef!!.id,
//                            "carbonCopy" to carbonCopyList,
//                            "confirmer" to confirmerList
//                        )
//                    )
                    Toast.makeText(this, "새 문서를 저장했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("FOR DEBUG", "Failed with: ", task.exception)
            }
        }
    }

    private fun load() {
        val intent = Intent(applicationContext, SavedDocumentsActivity::class.java)
        startActivityForResult(intent, 200)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (mIntentListener != null) {
                mIntentListener!!.onIntent(data, resultCode)
            }
        } else if (requestCode == 200 && data != null){
            val docData = data?.getSerializableExtra("documentData") as Document
            val documentId = docData.documentId
            documentRef = db.document("/documents/${documentId}")
            binding.title.setText(docData.title)
            Toast.makeText(this, "저장된 문서를 불러왔습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}