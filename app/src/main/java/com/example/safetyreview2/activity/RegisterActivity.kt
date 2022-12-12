package com.example.safetyreview2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.safetyreview2.R
import com.example.safetyreview2.databinding.ActivityLoginBinding
import com.example.safetyreview2.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val ranks: MutableList<String> = mutableListOf(
            "하사", "중사", "상사", "원사", "준위",
            "소위", "중위", "대위",
            "소령", "중령", "대령",
            "준장", "소장", "중장", "대장"
        )
        ranks.reverse()
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_register_article , ranks)
        binding.rankDropdownMenu.setAdapter(arrayAdapter)
        binding.rankDropdownMenu.dropDownHeight = 700

        val passwordTextView = binding.password
        val passwordCheckTextView = binding.passwordCheck

        passwordCheckTextView.doOnTextChanged { text, _, _, _ ->
            var password = passwordTextView.text.toString()

            if (text.toString().isNotEmpty()) {
                if (password != text.toString()) {
                    passwordCheckTextView.error = "비밀번호가 일치하지 않습니다."
                } else if (password == text.toString()) {
                    passwordCheckTextView.error = null
                    passwordCheckTextView.text
                }
            } else
                passwordCheckTextView.error = null
        }
        passwordTextView.doOnTextChanged { text, _, _, _ ->
            var passwordCheck = passwordCheckTextView.text.toString()

            if (passwordCheck.isNotEmpty()) {
                if (passwordCheck != text.toString()) {
                    passwordCheckTextView.error = "비밀번호가 일치하지 않습니다."
                } else if (passwordCheck == text.toString()){
                    passwordCheckTextView.error = null
                }
            }
        }

        binding.completeButton.setOnClickListener {
            val email = findViewById<TextInputEditText>(R.id.email).text.toString()
            val password = findViewById<TextInputEditText>(R.id.password).text.toString()
            createAccount(email, password)
        }

        binding.finish.setOnClickListener { finish() }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val name = binding.name.text.toString()
                    val rank = binding.rankDropdownMenu.text.toString()
                    val uid = auth.currentUser?.uid.toString()
                    val position = binding.position.text.toString()

                    db.collection("users").document(uid)
                        .set(
                            hashMapOf(
                                "name" to name,
                                "rank" to rank,
                                "email" to email,
                                "position" to position,
                                "carbonCopyDocument" to ArrayList<String>(),
                                "confirmDocument" to ArrayList<String>(),
                                "savedDocumentList" to ArrayList<String>()
                            )
                        )

                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterActivity", "회원가입 실패 ${task.exception}")
                }
            }
    }
}