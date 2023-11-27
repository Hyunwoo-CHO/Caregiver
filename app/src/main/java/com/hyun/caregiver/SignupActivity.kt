package com.hyun.caregiver

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hyun.caregiver.database.User
import com.hyun.caregiver.databinding.ActivitySignupBinding
import com.hyun.caregiver.repository.MyRepository

class SignupActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.database
    private val myRef = db.getReference("Caregiver")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener{
            finish()
        }
        binding.btnSignup.setOnClickListener {
            if (binding.inputEmail.text != null && binding.inputPassword.text != null) {
                if (binding.inputPassword.text.toString().equals(binding.inputConfirmPassword.text.toString())) {
                    createAccount(binding.inputEmail.text.toString(), binding.inputPassword.text.toString(), binding.inputName.text.toString())
                    val intent = Intent(this, LoginActivity::class.java)
                    //intent.putExtra("name", binding.inputName.text.toString())
                    startActivity(intent)
                }
                else {
                    Toast.makeText(
                        baseContext,
                        "비밀번호를 다시 확인해주세요",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            } else {
                Toast.makeText(
                    baseContext,
                    "빈 칸이 없는 지 확인해주세요",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun createAccount(email: String, password: String, nickname: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        baseContext,
                        "가입 성공",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val uid = task.getResult().user!!.uid
                    myRef.child("user").child(uid).setValue(User(uid, email, nickname, "", false))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "가입 실패",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}