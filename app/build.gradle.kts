plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.hsilveredu.caregiver"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.hsilveredu.caregiver"
        minSdk = 24
        targetSdk = 33
        versionCode = 28
        versionName = "28.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    /**  room dependencies  **/
    implementation("androidx.room:room-runtime:2.5.0")
    kapt("androidx.room:room-compiler:2.5.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /**  retrofit2  **/
    implementation("com.squareup.retrofit2:retrofit:2.7.1")
    implementation("com.squareup.retrofit2:converter-gson:2.7.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.3.1")
    /**  Glide library  **/
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.9.0")
    /**  Google Oauth client  **/
    implementation("com.google.api-client:google-api-client:1.30.4")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.30.6")
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0")
    /**  recyclerview  **/
    implementation("androidx.recyclerview:recyclerview:1.3.0-rc01")
    /**  coroutines  **/
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    /**  kakao login  **/
    implementation("com.kakao.sdk:v2-user:2.18.0")
    /**  naver login  **/
    implementation("com.navercorp.nid:oauth-jdk8:5.9.0")
    /**  firebase login  **/
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    /**  google play payment  **/
    implementation ("com.android.billingclient:billing:6.1.0")
    /**  subsampling image  **/
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    /**  recaptcha  **/
    implementation ("com.google.android.recaptcha:recaptcha:18.4.0")
}