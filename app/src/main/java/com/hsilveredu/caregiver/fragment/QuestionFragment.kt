package com.hsilveredu.caregiver.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hsilveredu.caregiver.adapter.ListviewAdapter
import com.hsilveredu.caregiver.adapter.RecyclerAdapter
import com.hsilveredu.caregiver.MainActivity
import com.hsilveredu.caregiver.MainViewModel
import com.hsilveredu.caregiver.MainViewModelFactory
import com.hsilveredu.caregiver.R
import com.hsilveredu.caregiver.databinding.FragmentQuestionBinding
import com.hsilveredu.caregiver.dialog.CommentDialog

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dataAdapter: ListviewAdapter
    private lateinit var listAdapter: RecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val questionViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(MainViewModel::class.java)

        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var qid = ""
        var answer = 0
        var comment = ""
        var comment_img = ""
        var solved = false

        (activity as MainActivity).onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                solved = false
                (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_question_to_nav_example)
            }
        })

        questionViewModel.category.observe(viewLifecycleOwner) {
            binding.testCategory.text = it
        }

        //observe viewmodel to get question  content
        val textView: TextView = binding.question
        val imgView: ImageView = binding.img

        questionViewModel.indexlist.observe(viewLifecycleOwner) {
            listAdapter = RecyclerAdapter(it)
            binding.itemRecycler.adapter = listAdapter
            binding.itemRecycler.layoutManager = LinearLayoutManager((activity as MainActivity), RecyclerView.HORIZONTAL, false)
            listAdapter.setItemClickListener(object : RecyclerAdapter.onItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    val result = listAdapter.data[position]
                    questionViewModel.indexQuestion(result)
                }
            })
        }

        questionViewModel.quest.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                solved = it.solved
                val layout_manager = binding.itemRecycler.layoutManager!!
                layout_manager.scrollToPosition(it.number - 1)
                textView.text = it.number.toString() + ". " + it.title
                if (it.img != "") {
                    Glide.with((activity as MainActivity)).load(it.img).into(imgView)
                    imgView.layoutParams.height = 300
                } else {
                    imgView.setImageResource(0)
                    imgView.layoutParams.height = 0
                }
                qid = it.qid
                answer = it.answer
                comment = it.comment
                comment_img = it.comment_img
                val data_list = ArrayList<String>()
                val elementToAdd = listOf(it.opt_a, it.opt_b, it.opt_c, it.opt_d, it.opt_e)
                data_list.addAll(elementToAdd)
                dataAdapter = ListviewAdapter((activity as MainActivity), data_list)
                binding.questionMenu.adapter = dataAdapter
                if (solved) {
                    questionViewModel.getPersonal(qid)
                }
            }
        }

        //check the answer by touching event of listview adapter
        binding.questionMenu.setOnItemClickListener { dataAdapter, view, position, id ->
            for (i in 0..4) {
                val initList = binding.questionMenu.getChildAt(i)
                initList.setBackgroundResource(R.drawable.listview_unclicked)
                initList.findViewById<ImageFilterView>(R.id.symbol).alpha = 0.0f
            }
            if (answer == position + 1) {
                questionViewModel.insertAnswer(qid, answer, position + 1, true)
                val selected = binding.questionMenu.getChildAt(position)
                selected.setBackgroundResource(R.drawable.listview_clicked)
                selected.findViewById<ImageFilterView>(R.id.symbol).alpha = 1.0f
//                 add listview color update and comment
            }
            else {
                questionViewModel.insertAnswer(qid, answer, position + 1, false)
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

        // for remember the answer
        questionViewModel.personal.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let{
                if (solved) {
                    Log.d("qid", qid)
                    Log.d("personal", it.qid)
                    val selected = binding.questionMenu.getChildAt(it.m_answer - 1)
                    val image = selected.findViewById<ImageFilterView>(R.id.symbol)
                    if (it.correction) {
                        selected.setBackgroundResource(R.drawable.listview_clicked)
                        image.alpha = 1.0f
                    } else {
                        val answer_item = binding.questionMenu.getChildAt(answer - 1)
                        val answer_image = answer_item.findViewById<ImageFilterView>(R.id.symbol)
                        selected.setBackgroundResource(R.drawable.listview_wrong)
                        image.setImageResource(R.drawable.red_check)
                        image.alpha = 1.0f
                        answer_item.setBackgroundResource(R.drawable.listview_clicked)
                        answer_image.alpha = 1.0f
                    }
                }
            }
        }

        binding.commentBtn.setOnClickListener {
            val dialog = CommentDialog(comment, comment_img)
            // 알림창이 띄워져있는 동안 배경 클릭 막기
            dialog.isCancelable = false
            dialog.show((activity as MainActivity).supportFragmentManager, "Comment Dialog")
        }

        binding.previousQuestion.setOnClickListener {
            questionViewModel.previousQuestion()
        }

        binding.nextQuestion.setOnClickListener {
            questionViewModel.nextQuestion()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}