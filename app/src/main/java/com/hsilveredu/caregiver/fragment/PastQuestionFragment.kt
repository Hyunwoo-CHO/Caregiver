package com.hsilveredu.caregiver.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hsilveredu.caregiver.adapter.CategoryAdapter
import com.hsilveredu.caregiver.MainActivity
import com.hsilveredu.caregiver.MainViewModel
import com.hsilveredu.caregiver.MainViewModelFactory
import com.hsilveredu.caregiver.R
import com.hsilveredu.caregiver.databinding.FragmentPastquestionBinding
import com.hsilveredu.caregiver.dialog.ConfirmDialog
import com.hsilveredu.caregiver.dialog.FullscreenDialog

class PastQuestionFragment: Fragment() {
    private var _binding: FragmentPastquestionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dataAdapter: CategoryAdapter
    private val db = Firebase.database
    private val myRef = db.getReference("Caregiver")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pastquestionViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(MainViewModel::class.java)

        _binding = FragmentPastquestionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as MainActivity).onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_pastquestion_to_nav_home)
            }
        })

        pastquestionViewModel.getCategory("모의고사")

        pastquestionViewModel.catelist.observe(viewLifecycleOwner) {
            val data_list = ArrayList<String>()
            data_list.addAll(it)
            dataAdapter = CategoryAdapter((activity as MainActivity), data_list)
            binding.categoryListview.adapter = dataAdapter
        }

        binding.categoryListview.setOnItemClickListener { dataAdapter, view, position, id->
            myRef.child("user").child((activity as MainActivity).uid).get().addOnSuccessListener {
                if (it.child("payment").value.toString().equals("true") or dataAdapter.getItemAtPosition(position).toString().equals("1회차 모의고사") or dataAdapter.getItemAtPosition(position).toString().equals("2회차 모의고사") ) {
                    val dialog = ConfirmDialog(dataAdapter.getItemAtPosition(position).toString() + "를 시작하겠습니까?")
                    // 알림창이 띄워져있는 동안 배경 클릭 막기
                    dialog.isCancelable = false
                    dialog.setItemClickListener(object : ConfirmDialog.ItemClickListener{
                        override fun onClick() {
                            pastquestionViewModel.testQuestion(dataAdapter.getItemAtPosition(position).toString()) // store the category uid list in viewmodel
                            (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_pastquestion_to_nav_test)
                        }
                    })
                    dialog.show((activity as MainActivity).supportFragmentManager, "Confirm Dialog")
                } else {
                    val dialog = FullscreenDialog()
                    // 알림창이 띄워져있는 동안 배경 클릭 막기
                    dialog.isCancelable = false
                    dialog.setItemClickListener(object : FullscreenDialog.ItemClickListener{
                        override fun onClick() {
                            //결제 api
                            (activity as MainActivity).paymentFlow()
                        }
                    })
                    dialog.show((activity as MainActivity).supportFragmentManager, "Fullscreen Dialog")
                }
            }.addOnFailureListener {
                Log.e("firebase_example", "Error getting data", it)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "past question fragment destroyed")
        _binding = null
    }
}