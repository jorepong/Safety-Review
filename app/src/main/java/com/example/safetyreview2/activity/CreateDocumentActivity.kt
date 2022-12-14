package com.example.safetyreview2.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safetyreview2.*
import com.example.safetyreview2.adapter.CreateDocumentRecyclerAdapter
import com.example.safetyreview2.adapter.CustomArrayAdapter
import com.example.safetyreview2.data.ChecklistItem
import com.example.safetyreview2.data.Document
import com.example.safetyreview2.data.User
import com.example.safetyreview2.databinding.ActivityCreateDocumentBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.util.Date


class CreateDocumentActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCreateDocumentBinding
    private lateinit var documentReference: DocumentReference
    private lateinit var currentUserId: String
    private val ccUsers = ArrayList<User>()
    private val confirmerUsers = ArrayList<User>()
    private val database = Firebase.firestore
    private var document = Document()
    private var comparisonDocument = Document()
    private val checklist = ArrayList<ChecklistItem>()
    var touchHelper: ItemTouchHelper? = null
    var mIntentListener: OnIntentReceived? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        currentUserId = auth.currentUser?.uid.toString()
        documentReference = database.collection("documents").document()
        updateDocument()
        comparisonDocument = document.copy()
        setOnItemClickListener()

        checklist.add(ChecklistItem(isDummy = true))
        val recyclerView = binding.recyclerView
        val adapter = CreateDocumentRecyclerAdapter(this, checklist)
        val callback = TouchHelperCallback(adapter)
        adapter.setHasStableIds(true)
        mIntentListener = adapter
        touchHelper = ItemTouchHelper(callback)
        touchHelper?.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val slidePanel = binding.slidingUpPanelLayout

        binding.showPanel.setOnClickListener {
            val state = slidePanel.panelState
            // ?????? ????????? ?????? ??????
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


        val userList = getOtherUsers()
        binding.carbonCopy.setAdapter(CustomArrayAdapter(this, R.layout.dropdown_article, userList))
        binding.confirmer.setAdapter(CustomArrayAdapter(this, R.layout.dropdown_article, userList))

        binding.save.setOnClickListener {
            writeDocumentData()
            writeAuthorInfo()
        }
        binding.load.setOnClickListener { openDocumentListActivity() }
        binding.backButton.setOnClickListener {
            navigateBack()
        }
        binding.preview.setOnClickListener {
            preview(ccUsers, confirmerUsers)
        }
    }

    private fun setOnItemClickListener() {
        val carbonCopyChipGroup = binding.carbonCopyChipGroup
        val confirmerChipGroup = binding.confirmerChipGroup

        binding.carbonCopy.setOnItemClickListener { parent, _, position, _ ->
            val selectedUser = parent?.getItemAtPosition(position) as User
            val userChip = Chip(this)
            userChip.text = "${selectedUser.rank} ${selectedUser.name}"
            userChip.setChipIconResource(R.drawable.ic_baseline_person_24)
            userChip.isCloseIconVisible = true
            userChip.setOnCloseIconClickListener { carbonCopyChipGroup.removeView(userChip as View) }
            carbonCopyChipGroup.addView(userChip as View)
            binding.carbonCopy.text = null
            ccUsers.add(selectedUser)
        }

        binding.confirmer.setOnItemClickListener { parent, _, position, _ ->
            val selectedUser = parent?.getItemAtPosition(position) as User
            val userChip = Chip(this)
            userChip.text = "${selectedUser.rank} ${selectedUser.name}"
            userChip.setChipIconResource(R.drawable.ic_baseline_person_24)
            userChip.isCloseIconVisible = true
            userChip.setOnCloseIconClickListener { confirmerChipGroup.removeView(userChip as View) }
            confirmerChipGroup.addView(userChip as View)
            binding.confirmer.text = null
            confirmerUsers.add(selectedUser)
        }
    }

    private fun navigateBack() {
        updateDocument()

        if(document != comparisonDocument)
        {
            AlertDialog.Builder(this)
                .setMessage("?????? ?????? ????????? ????????????.")
                .setPositiveButton("?????????", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        finish()
                    }
                })
                .setNegativeButton("??????", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                })
                .create()
                .show()
        } else {
            finish()
        }
    }

    private fun preview(carbonCopyList: ArrayList<User>, confirmerList: ArrayList<User>) {
        updateDocument()
        val intent = Intent(applicationContext, DocumentPreviewActivity::class.java)
        intent.putExtra("data", document)
        startActivityForResult(intent, 300)
    }

    private fun getOtherUsers(): ArrayList<User> {
        val userList = ArrayList<User>()
        database.collection("users").get().addOnSuccessListener { dataSnapShot ->
            if(dataSnapShot != null) {
                for(item in dataSnapShot) {
                    val user = item.toObject<User>()
                    if(user.uid != currentUserId)
                        userList.add(user)
                }
            }
        }
        return userList
    }

    // ?????? ????????? ??????
    private fun writeDocumentData() {
        documentReference.get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                updateDocument()
                // ????????? ????????? ?????? ??? ?????? ????????????
                if(task.result.exists()) {
                    documentReference.update(
                        hashMapOf(
                            "title" to document.title,
                            "modifiedTime" to FieldValue.serverTimestamp(),
                            "carbonCopy" to ccUsers,
                            "confirmer" to confirmerUsers
                        )
                    )
                    Toast.makeText(applicationContext, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                } else { // ????????? ???????????? ?????? ?????? ??? ??? ?????? ??????
                    documentReference.set(document)
                    Toast.makeText(applicationContext, "??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ????????? ?????? ??????
    private fun writeAuthorInfo() {
        // ??? ?????? ????????? ??????
        if(documentReference != null)
            database.collection("users").document(auth.currentUser?.uid.toString())
                .update("savedDocumentList", FieldValue.arrayUnion(documentReference.id))
    }

    private fun openDocumentListActivity() {
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
            documentReference = database.document("/documents/${documentId}")
            binding.title.setText(docData.title)
            Toast.makeText(this, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
        } else if (resultCode == 300) {
            finish()
        }
    }

    private fun updateDocument() {
        var titleText = binding.title.text.toString()
        val descriptionText = binding.description.text

        if(titleText.isBlank())
            titleText = "?????? ?????? ??????"

        document = Document(titleText, Date(), Date(), documentReference!!.id, ccUsers, confirmerUsers, auth.currentUser?.uid.toString(), checklist)
    }
}