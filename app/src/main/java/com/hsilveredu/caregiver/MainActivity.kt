package com.hsilveredu.caregiver

import android.content.ContentValues
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
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.SkuDetails
import com.bumptech.glide.Glide
import com.google.common.collect.ImmutableList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hsilveredu.caregiver.databinding.ActivityMainBinding
import com.hsilveredu.caregiver.dialog.CommentDialog
import com.hsilveredu.caregiver.dialog.FullscreenDialog
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.immutableListOf

class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var profile = ""
    private var nickname = "사용자"
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.database
    private val myRef = db.getReference("Caregiver")
    var uid = ""

    // In-App payment variable
    private lateinit var billingCilent: BillingClient
    private var productList: List<ProductDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBillingClient()

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

        CoroutineScope(Dispatchers.Main).launch {
            mainviewmodel.getJsonFile() // fetch new data from server by viewmodel
            val intent = Intent(this@MainActivity, LoadingActivity::class.java)
            startActivity(intent)
            delay(2000)
            mainviewmodel.getSolved()
        }
    }

    override fun onResume() {
        super.onResume()
        billingCilent.queryPurchasesAsync(BillingClient.ProductType.INAPP) { billingResult, purchaseList ->
            if( billingResult.responseCode == BillingClient.BillingResponseCode.OK ) {
                purchaseList.forEach {
                    handlePurchase(it)
                }
            }
        }
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
                } else if (NaverIdLoginSDK.getAccessToken() != null) {
                    NaverIdLoginSDK.logout()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.action_upgrade -> {
                myRef.child("user").child(uid).get().addOnSuccessListener {
                    if (it.child("payment").value.toString().equals("false")) {
                        val dialog = FullscreenDialog()
                        // 알림창이 띄워져있는 동안 배경 클릭 막기
                        dialog.isCancelable = false
                        dialog.setItemClickListener(object : FullscreenDialog.ItemClickListener{
                            override fun onClick() {
                                //결제 api
                                paymentFlow()
                            }
                        })
                        dialog.show(this.supportFragmentManager, "Fullscreen Dialog")
                    } else {
                        val dialog = CommentDialog("이미 PRO 버전을 구매했습니다!", "")
                        // 알림창이 띄워져있는 동안 배경 클릭 막기
                        dialog.isCancelable = false
                        dialog.show(this.supportFragmentManager, "Comment Dialog")
                    }
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

        if (auth.currentUser != null) {
            uid = auth.currentUser!!.uid
            myRef.child("user").child(uid).get().addOnSuccessListener {
                nickname = it.child("nickname").value.toString()
                profile = it.child("profile").value.toString()
                if (profile != "") {
                    Glide.with(this@MainActivity).load(profile).into(imageview)
                }
                nickview.text = nickname
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        } else if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e(TAG, "토큰 정보 보기 실패", error)
                } else if (tokenInfo != null) {
                    Log.i(TAG, "토큰 정보 보기 성공")
                    uid = tokenInfo.id.toString()
                    myRef.child("user").child(uid).get().addOnSuccessListener {
                        nickname = it.child("nickname").value.toString()
                        profile = it.child("profile").value.toString()
                        if (profile != "") {
                            Glide.with(this@MainActivity).load(profile).into(imageview)
                        }
                        nickview.text = nickname
                    }.addOnFailureListener{
                        Log.e("firebase", "Error getting data", it)
                    }
                }
            }
        } else if (NaverIdLoginSDK.getAccessToken() != null) {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(result: NidProfileResponse) {
                    uid = result.profile?.id.toString()
                    myRef.child("user").child(uid).get().addOnSuccessListener {
                        nickname = it.child("nickname").value.toString()
                        profile = it.child("profile").value.toString()
                        if (profile != "") {
                            Glide.with(this@MainActivity).load(profile).into(imageview)
                        }
                        nickview.text = nickname
                    }.addOnFailureListener{
                        Log.e("firebase", "Error getting data", it)
                    }
                }
                override fun onFailure(httpStatus: Int, message: String) {
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }
    }

    /**
     * Billing Client 초기화 ->
     * BliiingClient : 결제 라이브러리 통신 인터페이스
     */
    private fun initBillingClient() {
        billingCilent = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingCilent.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                //연결이 종료될 시 재시도 요망
                Log.d(TAG, "연결 실패")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // 연결 성공
                    Log.d(TAG, "연결 성공")
                    //Suspend 함수는 반드시 코루틴 내부에서 실행
                    CoroutineScope(Dispatchers.Main).launch {
                        queryProductDetails()
                    }
                }
            }
        })
    }

    /**
     * 구매 가능 목록 호출
     * 필요하다면 API 구분 필요
     * */
    private fun queryProductDetails() {
        val productList =
            immutableListOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("hsilver.pro")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("pro_upgrade")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingCilent.queryProductDetailsAsync(params) {
                billingResult,
                mutableList ->
            Log.d("product", mutableList.toString())
            this.productList = mutableList
        }

    }

    fun paymentFlow() {
        val flowProductDetailParams1 = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productList[0])
            .build()


        val list : MutableList<BillingFlowParams.ProductDetailsParams> = mutableListOf()
        list.add(flowProductDetailParams1)

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(list)
            .build()

        val responseCode = billingCilent.launchBillingFlow(this, flowParams).responseCode
        Log.d(TAG, responseCode.toString())
        Log.d(TAG, BillingClient.BillingResponseCode.OK.toString())
  }


    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.d(TAG, "???? ${billingResult.responseCode}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                Toast.makeText(this@MainActivity, "PRO 버전 업그레이드 성공", Toast.LENGTH_SHORT).show()
                // 거래 성공 코드
                myRef.child("user").child(uid).child("payment").setValue(true)
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this@MainActivity, "유저 취소", Toast.LENGTH_SHORT).show()
            // 유저 취소 errorcode
        } else {
            Toast.makeText(this@MainActivity, "결제 실패", Toast.LENGTH_SHORT).show()
            // 이외의 오류 처리
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        //BillingClient#queryPurchasesAsync 혹은 onPurchasesUpdated에서 검색된 구매 목록
        //제품에 대한 확인
        //구매토큰의 권한 확인 필요
        //유저에게 권한 부여
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingCilent.consumeAsync(consumeParams) { billingResult, _ ->
            //소비 처리에 대한 결과 처리
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG,"소모 성공")
            } else {
                Log.d(TAG,"소모 실패")
            }
        }
    }
}