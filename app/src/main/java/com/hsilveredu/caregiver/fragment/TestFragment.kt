package com.hsilveredu.caregiver.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hsilveredu.caregiver.ImageActivity
import com.hsilveredu.caregiver.adapter.ListviewAdapter
import com.hsilveredu.caregiver.adapter.RecyclerAdapter
import com.hsilveredu.caregiver.MainActivity
import com.hsilveredu.caregiver.MainViewModel
import com.hsilveredu.caregiver.MainViewModelFactory
import com.hsilveredu.caregiver.R
import com.hsilveredu.caregiver.databinding.FragmentTestBinding
import com.hsilveredu.caregiver.dialog.ConfirmDialog
import com.hsilveredu.caregiver.dialog.FinishDialog
import java.util.concurrent.TimeUnit

class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dataAdapter: ListviewAdapter
    private lateinit var listAdapter: RecyclerAdapter
    private lateinit var countDownTimer: CountDownTimer
    private var category = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val TestViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(MainViewModel::class.java)

        _binding = FragmentTestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as MainActivity).onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_test_to_nav_pastquestion)
            }
        })

        TestViewModel.category.observe(viewLifecycleOwner) {
            binding.testCategory.text = it
            category = it
        }

        // 10분 동안 1초마다 감소하는 타이머
        countDownTimer = object : CountDownTimer(5400000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.testClock.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.testClock.text = "타이머 종료"
                TestViewModel.gradingTest()
                val dialog = FinishDialog("시험 시간이 종료되었습니다.")
                // 알림창이 띄워져있는 동안 배경 클릭 막기
                dialog.isCancelable = false
                dialog.setItemClickListener(object : FinishDialog.ItemClickListener{
                    override fun onClick() {
                        (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_test_to_nav_score)
                    }
                })
                dialog.show((activity as MainActivity).supportFragmentManager, "Finish Dialog")
            }
        }

        countDownTimer.start()

        var number = 0

        //observe viewmodel to get Test  content
        val textView: TextView = binding.question
        val imgView: ImageView = binding.img
        var imgURL = ""

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
            it.getContentIfNotHandled()?.let {
                // Clear all selections
                clearAllSelections(binding.questionMenu)
                val layout_manager = binding.itemRecycler.layoutManager!!
                layout_manager.scrollToPosition(it.number - 1)
                textView.text = (it.number.toString() + ". " + it.title).replace(" ", "\u00A0")
                imgURL = it.img
                if (it.img != "") {
                    Glide.with((activity as MainActivity)).load(it.img).into(imgView)
                    imgView.layoutParams.height = 300
                } else {
                    imgView.setImageResource(0)
                    imgView.layoutParams.height = 0
                }
                number = it.number
                binding.dataName1.text = it.opt_a
                binding.dataName2.text = it.opt_b
                binding.dataName3.text = it.opt_c
                binding.dataName4.text = it.opt_d
                binding.dataName5.text = it.opt_e
                TestViewModel.getTestAnswer(number)
            }
        }

        //check the answer by touching event of listview adapter
        binding.questionMenu.children.forEachIndexed { index: Int, view: View ->
            if (view is TableRow) {
                view.setOnClickListener {
                    // Clear all selections
                    clearAllSelections(binding.questionMenu)

                    // Get the row number
                    val selectedRowNumber = index + 1
                    TestViewModel.testAnswer(number, selectedRowNumber)
                    view.setBackgroundResource(R.drawable.listview_selected)
                }
            }
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

        binding.finishBtn.setOnClickListener {
            TestViewModel.gradingTest()
            //Dialog for show score
            val dialog = ConfirmDialog("시험을 종료하시겠습니까?")
            // 알림창이 띄워져있는 동안 배경 클릭 막기
            dialog.isCancelable = false
            dialog.setItemClickListener(object : ConfirmDialog.ItemClickListener{
                override fun onClick() {
                    (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_test_to_nav_score)
                }
            })
            dialog.show((activity as MainActivity).supportFragmentManager, "Confirm Dialog")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer.cancel()
    }

    private fun clearAllSelections(tableLayout: TableLayout) {
        tableLayout.children.forEach { view ->
            if (view is TableRow) {
                view.setBackgroundResource(R.drawable.listview_unclicked)
            }
        }
    }
}