package com.hsilveredu.caregiver

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hsilveredu.caregiver.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hsilveredu.caregiver.database.User
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.database
    private val myRef = db.getReference("Caregiver")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        /** Naver Login Module Initialize */
        val naverClientId = getString(R.string.social_login_info_naver_client_id)
        val naverClientSecret = getString(R.string.social_login_info_naver_client_secret)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        val kakaoAppKey = getString(R.string.kakao_app_key)
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)
        KakaoSdk.init(this, kakaoAppKey)

        /** firebase auto login **/
        if (auth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
            finish();
        }
        /** kakao auto login **/
        else if (AuthApiClient.instance.hasToken()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        /** naver auto login **/
        else if (NaverIdLoginSDK.getAccessToken() != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.loginBtn.setOnClickListener{
            Log.d("email & password", binding.email.text.toString()+binding.password.text.toString())
            if (!binding.email.text!!.trim().isEmpty() && !binding.password.text!!.trim().isEmpty()) {
                auth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
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
                Toast.makeText(
                    baseContext,
                    "이메일과 비밀번호를 입력해주세요.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        binding.btnSignup.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.kakaoLogin.setOnClickListener{
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                    val info = MutableList(3, { i -> "" })
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            info[0] = user.id.toString()
                            info[1] = user.kakaoAccount?.profile?.nickname!!            //nickname
                            info[2] = user.kakaoAccount?.profile?.thumbnailImageUrl!!   //profile
                        }
                        myRef.child("user").orderByChild("uid").equalTo(info[0])
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        myRef.child("user").child(info[0]).setValue(User(info[0], info[1], info[2], false))
                                    } else {
                                        Log.e(TAG, "이미 등록된 계정")
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(applicationContext,
                                        databaseError.message,
                                        Toast.LENGTH_SHORT).show()
                                }
                            })
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        binding.naverLogin.setOnClickListener {
            var naverToken :String? = ""

            val profileCallback = object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(result: NidProfileResponse) {
                    Toast.makeText(this@LoginActivity, "네이버 아이디 로그인 성공!", Toast.LENGTH_SHORT).show()
                    val info = MutableList(3, { i -> "" })
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            info[0] = result.profile?.id.toString()
                            info[1] = result.profile?.nickname.toString()
                            info[2] = result.profile?.profileImage.toString()
                            myRef.child("user").orderByChild("uid").equalTo(info[0])
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            myRef.child("user").child(info[0]).setValue(User(info[0], info[1], info[2], false))
                                        } else {
                                            Log.e(TAG, "이미 등록된 계정")
                                        }
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Toast.makeText(applicationContext,
                                            databaseError.message,
                                            Toast.LENGTH_SHORT).show()
                                    }
                                })
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                        override fun onFailure(httpStatus: Int, message: String) {
                        }
                        override fun onError(errorCode: Int, message: String) {
                            onFailure(errorCode, message)
                        }
                    })
                }
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(this@LoginActivity, "errorCode: ${errorCode}\n" +
                            "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            /** OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다. */
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    naverToken = NaverIdLoginSDK.getAccessToken()

                    //로그인 유저 정보 가져오기
                    NidOAuthLogin().callProfileApi(profileCallback)
                }
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(this@LoginActivity, "errorCode: ${errorCode}\n" +
                            "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
        }
    }
}