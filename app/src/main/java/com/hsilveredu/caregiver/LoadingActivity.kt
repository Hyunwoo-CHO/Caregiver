package com.hsilveredu.caregiver

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.bumptech.glide.Glide
import com.hsilveredu.caregiver.databinding.ActivityLoadingBinding


class LoadingActivity: Activity() {

    private lateinit var binding: ActivityLoadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this@LoadingActivity).load(R.drawable.loading).into(binding.loading)

        startLoading()
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed(Runnable { finish() }, 3000)
    }
}