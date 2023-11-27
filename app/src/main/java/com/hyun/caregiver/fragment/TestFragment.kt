package com.hyun.caregiver.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.animation.content.Content
import com.bumptech.glide.Glide
import com.hyun.caregiver.adapter.ListviewAdapter
import com.hyun.caregiver.adapter.RecyclerAdapter
import com.hyun.caregiver.MainActivity
import com.hyun.caregiver.MainViewModel
import com.hyun.caregiver.MainViewModelFactory
import com.hyun.caregiver.R
import com.hyun.caregiver.databinding.FragmentTestBinding
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dataAdapter: ListviewAdapter
    private lateinit var listAdapter: RecyclerAdapter
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val TestViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(MainViewModel::class.java)

        _binding = FragmentTestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        TestViewModel.category.observe(viewLifecycleOwner) {
            binding.testCategory.text = it
        }

        // 10분 동안 1초마다 감소하는 타이머
        countDownTimer = object : CountDownTimer(4800000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.testClock.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.testClock.text = "타이머 종료"
                TestViewModel.gradingTest()
            }
        }

        countDownTimer.start()

        TestViewModel.test_score.observe(viewLifecycleOwner) {
            //Dialog for show score
            val score = it * 1.25f
        }

        var number = 0

        //observe viewmodel to get Test  content
        val textView: TextView = binding.question
        val imgView: ImageView = binding.img

        TestViewModel.indexlist.observe(viewLifecycleOwner) {
            listAdapter = RecyclerAdapter(it)
            binding.itemRecycler.adapter = listAdapter
            binding.itemRecycler.layoutManager = LinearLayoutManager((activity as MainActivity), RecyclerView.HORIZONTAL, false)
            listAdapter.setItemClickListener(object : RecyclerAdapter.onItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    val result = listAdapter.data[position]
                    TestViewModel.indexQuestion(result)
                }
            })
        }

        TestViewModel.quest.observe(viewLifecycleOwner) {
            val layout_manager = binding.itemRecycler.layoutManager!!
            layout_manager.scrollToPosition(it.number - 1)
            textView.text = it.number.toString() + ". " + it.title
            if (it.img != "") {
                Glide.with((activity as MainActivity)).load(it.img).override(1000,500).into(imgView)
//                imgView.layoutParams.height = 500
            } else {
                imgView.setImageResource(0)
                imgView.layoutParams.height = 0
            }
            number = it.number
            val data_list = ArrayList<String>()
            val elementToAdd = listOf("1. " + it.opt_a, "2. " + it.opt_b, "3. " + it.opt_c, "4. " + it.opt_d, "5. " + it.opt_e)
            data_list.addAll(elementToAdd)
            dataAdapter = ListviewAdapter((activity as MainActivity), data_list)
            binding.questionMenu.adapter = dataAdapter
            TestViewModel.getTestAnswer(number)
        }

        //check the answer by touching event of listview adapter
        binding.questionMenu.setOnItemClickListener { dataAdapter, view, position, id ->
            for (i in 0..4) {
                val initList = binding.questionMenu.getChildAt(i)
                initList.setBackgroundResource(R.drawable.listview_unclicked)
            }
            TestViewModel.testAnswer(number, position + 1)
            val selected = binding.questionMenu.getChildAt(position)
            selected.setBackgroundResource(R.drawable.listview_selected)
        }

        // for remember the answer
        TestViewModel.test_answer.observe(viewLifecycleOwner) {
            if (it != 0) {
                val selected = binding.questionMenu.getChildAt(it - 1)
                selected.setBackgroundResource(R.drawable.listview_selected)
            }
        }

        binding.previousQuestion.setOnClickListener {
            TestViewModel.previousQuestion()
        }

        binding.nextQuestion.setOnClickListener {
            TestViewModel.nextQuestion()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer.cancel()
    }
}