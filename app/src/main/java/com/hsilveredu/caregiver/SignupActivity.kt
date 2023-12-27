package com.hsilveredu.caregiver

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hsilveredu.caregiver.database.User
import com.hsilveredu.caregiver.databinding.ActivitySignupBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

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
            if (!binding.inputEmail.text!!.trim().isEmpty() && !binding.inputPassword.text!!.trim().isEmpty() && !binding.inputConfirmPassword.text!!.trim().isEmpty() && !binding.inputName.text!!.trim().isEmpty()) {
                if (binding.inputPassword.text.toString().equals(binding.inputConfirmPassword.text.toString())) {
                    createAccount(binding.inputEmail.text.toString(), binding.inputPassword.text.toString(), binding.inputName.text.toString())
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
                    myRef.child("user").child(uid).setValue(User(uid, nickname, "", false))
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
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