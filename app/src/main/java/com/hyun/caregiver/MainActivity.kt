package com.hyun.caregiver

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.hyun.caregiver.databinding.ActivityMainBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var profile = ""
    private var nickname = "사용자"
    private var email = "내 이메일"
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainviewmodel = ViewModelProvider(this, MainViewModelFactory(application)).get(MainViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_checknote, R.id.nav_example, R.id.nav_pastquestion
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val header = navView.getHeaderView(0)
        initProfile(header)

        mainviewmodel.getJsonFile() // fetch new data from server by viewmodel
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_logout -> {
                if (auth.getCurrentUser() != null) {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else if (AuthApiClient.instance.hasToken()) {
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                        }
                        else {
                            Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    NaverIdLoginSDK.logout()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun initProfile(header: View) {
        val imageview = header.findViewById<ImageView>(R.id.user_profile)
        val nickview = header.findViewById<TextView>(R.id.user_nickname)
        val emailview = header.findViewById<TextView>(R.id.user_email)

        if (auth.currentUser != null) {
            val user = auth.currentUser
            email = user?.email.toString()
            if (profile != "") {
                Glide.with(this@MainActivity).load(profile).into(imageview)
            }
            nickview.text = nickname
            emailview.text = email
        } else if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                }
                else if (user != null) {
                    profile = user.kakaoAccount?.profile?.thumbnailImageUrl!!
                    nickname = user.kakaoAccount?.profile?.nickname!!
                    email = user.kakaoAccount?.email!!
                    if (profile != "") {
                        Glide.with(this@MainActivity).load(profile).into(imageview)
                    }
                    nickview.text = nickname
                    emailview.text = email
                }
            }
        } else if (NaverIdLoginSDK.getAccessToken() != null) {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(result: NidProfileResponse) {
                    profile = result.profile?.profileImage.toString()
                    nickname = result.profile?.nickname.toString()
                    email = result.profile?.email.toString()
                    if (profile != "") {
                        Glide.with(this@MainActivity).load(profile).into(imageview)
                    }
                    nickview.text = nickname
                    emailview.text = email
                }
                override fun onFailure(httpStatus: Int, message: String) {
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }
    }
}