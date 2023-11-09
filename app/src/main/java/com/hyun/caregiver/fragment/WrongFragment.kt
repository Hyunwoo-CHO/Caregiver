package com.hyun.caregiver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.hyun.caregiver.adapter.ListviewAdapter
import com.hyun.caregiver.MainActivity
import com.hyun.caregiver.MainViewModel
import com.hyun.caregiver.MainViewModelFactory
import com.hyun.caregiver.R
import com.hyun.caregiver.databinding.FragmentWrongBinding

class WrongFragment : Fragment() {

    private var _binding: FragmentWrongBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dataAdapter: ListviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val wrongViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(MainViewModel::class.java)

        _binding = FragmentWrongBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var qid = 1
        var answer = 0
        var comment = ""
        var comment_img = ""
        var pressed = false

        //observe viewmodel to get question  content
        val textView: TextView = binding.question
        val imgView: ImageView = binding.img
        val cmtImgView: ImageFilterView = binding.commentImg
        var qnum = ""
        wrongViewModel.qnum.observe(viewLifecycleOwner) {
            qnum = it.toString()
        }
        wrongViewModel.quest.observe(viewLifecycleOwner) {
            textView.text = qnum + ". " + it.title
            if (it.img != "") {
                Glide.with((activity as MainActivity)).load(it.img).into(imgView)
            } else {
                imgView.setImageResource(0)
            }
            qid = it.uid!!
            answer = it.answer
            comment = it.comment
            comment_img = it.comment_img
            val data_list = ArrayList<String>()
            val elementToAdd = listOf("1. " + it.opt_a, "2. " + it.opt_b, "3. " + it.opt_c, "4. " + it.opt_d, "5. " + it.opt_e)
            data_list.addAll(elementToAdd)
            dataAdapter = ListviewAdapter((activity as MainActivity), data_list)
            binding.questionMenu.adapter = dataAdapter

            if (it.solved) {
                wrongViewModel.getPersonal(qid)
            }
        }

        // for remember the answer
//        wrongViewModel.personal.observe(viewLifecycleOwner) {
//            if (it.correction) {
//                val selected = binding.questionMenu.getChildAt(it.m_answer - 1)
//                selected.setBackgroundResource(R.drawable.listview_clicked)
//                selected.findViewById<ImageFilterView>(R.id.symbol).alpha = 1.0f
//            } else {
//                val selected = binding.questionMenu.getChildAt(it.m_answer - 1)
//                val answer_item = binding.questionMenu.getChildAt(answer - 1)
//                val image = selected.findViewById<ImageFilterView>(R.id.symbol)
//                val answer_image = answer_item.findViewById<ImageFilterView>(R.id.symbol)
//                selected.setBackgroundResource(R.drawable.listview_wrong)
//                image.setImageResource(R.drawable.red_check)
//                image.alpha = 1.0f
//                answer_item.setBackgroundResource(R.drawable.listview_clicked)
//                answer_image.alpha = 1.0f
//            }
//        }

        //check the answer by touching event of listview adapter
        binding.questionMenu.setOnItemClickListener { dataAdapter, view, position, id ->
            for (i in 0..4) {
                val initList = binding.questionMenu.getChildAt(i)
                initList.setBackgroundResource(R.drawable.listview_unclicked)
                initList.findViewById<ImageFilterView>(R.id.symbol).alpha = 0.0f
            }
            if (answer == position + 1) {
                wrongViewModel.insertAnswer(qid, answer, position + 1, true)
                val selected = binding.questionMenu.getChildAt(position)
                selected.setBackgroundResource(R.drawable.listview_clicked)
                selected.findViewById<ImageFilterView>(R.id.symbol).alpha = 1.0f
                // add listview color update and comment
            }
            else {
                wrongViewModel.insertAnswer(qid, answer, position + 1, false)
                // add listview color update and comment
                val selected = binding.questionMenu.getChildAt(position)
                val answer_item = binding.questionMenu.getChildAt(answer - 1)
                val image = selected.findViewById<ImageFilterView>(R.id.symbol)
                val answer_image = answer_item.findViewById<ImageFilterView>(R.id.symbol)
                selected.setBackgroundResource(R.drawable.listview_wrong)
                image.setImageResource(R.drawable.red_check)
                image.alpha = 1.0f
                answer_item.setBackgroundResource(R.drawable.listview_clicked)
                answer_image.alpha = 1.0f
            }
        }
        binding.commentBtn.setOnClickListener {
//            binding.comment.isVisible = true
//            binding.commentImg.isVisible = true
            if (pressed) {
                binding.comment.text = ""
                binding.commentImg.setImageResource(0)
                binding.commentBtn.text = "해설 보기"
            } else {
                binding.comment.text = comment
                if (comment_img != "") {
                    Glide.with((activity as MainActivity)).load(comment_img).into(cmtImgView)
                }
                binding.commentBtn.text = "해설 닫기"
            }
            pressed = !pressed
        }

        binding.previousQuestion.setOnClickListener {
            binding.comment.isVisible = false
            binding.commentImg.isVisible = false
            wrongViewModel.previousQuestion()
        }

        binding.nextQuestion.setOnClickListener {
            binding.comment.isVisible = false
            binding.commentImg.isVisible = false
            wrongViewModel.nextQuestion()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}