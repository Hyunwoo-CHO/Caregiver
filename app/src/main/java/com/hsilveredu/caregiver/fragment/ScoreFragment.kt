package com.hsilveredu.caregiver.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.hsilveredu.caregiver.MainActivity
import com.hsilveredu.caregiver.MainViewModel
import com.hsilveredu.caregiver.MainViewModelFactory
import com.hsilveredu.caregiver.R
import com.hsilveredu.caregiver.databinding.FragmentScoreBinding

class ScoreFragment() : Fragment() {

    // 뷰 바인딩 정의
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ScoreViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(
                MainViewModel::class.java)

        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        (activity as MainActivity).onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_score_to_nav_home)
            }
        })

        var correct_writ = 0
        var correct_prac = 0

        ScoreViewModel.category.observe(viewLifecycleOwner) {
            val str = it + "\n성적 확인"
            binding.title.text = str
        }

        ScoreViewModel.test_score.observe(viewLifecycleOwner) {
            correct_writ = it[0]
            correct_prac = it[1]
            if (correct_writ >= 21) {
                if (correct_prac >= 27) {
                    binding.imgCongrat.setImageResource(R.drawable.congrat)
                    binding.textCongrat.text = "합격을 축하합니다!"
                } else {
                    binding.scorePrac.setTextColor(Color.RED)
                    binding.imgCongrat.setImageResource(R.drawable.fail)
                    binding.textCongrat.text = "실기 점수가 부족합니다."
                }
            } else {
                binding.scoreWrit.setTextColor(Color.RED)
                if (correct_prac < 27) {
                    binding.scorePrac.setTextColor(Color.RED)
                    binding.imgCongrat.setImageResource(R.drawable.fail)
                    binding.textCongrat.text = "필기 & 실기 점수가 부족합니다."
                } else {
                    binding.imgCongrat.setImageResource(R.drawable.fail)
                    binding.textCongrat.text = "필기 점수가 부족합니다."
                }
            }

            val writ_score = (correct_writ.toFloat() / 35f) * 100f
            val prac_score = (correct_prac.toFloat() / 45f) * 100f

            binding.scoreWrit.text = writ_score.toInt().toString() + "%"
            binding.scorePrac.text = prac_score.toInt().toString() + "%"

            val writ_cor = "(" + correct_writ.toString() + "/35)"
            val prac_cor = "(" + correct_prac.toString() + "/45)"
            binding.correctWrit.text = writ_cor
            binding.correctPrac.text = prac_cor
        }

        binding.gohome.setOnClickListener {
            (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_score_to_nav_home)
        }

        val banner = binding.banner
        Glide.with(this).load(R.drawable.banner).into(banner)

        banner.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sarangnc.co.kr/05comm/sub1-1.htm?no=1334"))
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}