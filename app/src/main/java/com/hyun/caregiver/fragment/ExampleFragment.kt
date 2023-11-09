package com.hyun.caregiver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.hyun.caregiver.adapter.CategoryAdapter
import com.hyun.caregiver.MainActivity
import com.hyun.caregiver.MainViewModel
import com.hyun.caregiver.MainViewModelFactory
import com.hyun.caregiver.R
import com.hyun.caregiver.databinding.FragmentExampleBinding

class ExampleFragment : Fragment() {

    private var _binding: FragmentExampleBinding? = null
    private lateinit var dataAdapter: CategoryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val exampleViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(MainViewModel::class.java)

        _binding = FragmentExampleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        exampleViewModel.getCategory("예상문제")

        exampleViewModel.catelist.observe(viewLifecycleOwner) {
            val data_list = ArrayList<String>()
            data_list.addAll(it)
            dataAdapter = CategoryAdapter((activity as MainActivity), data_list)
            binding.categoryListview.adapter = dataAdapter
        }

        binding.categoryListview.setOnItemClickListener { dataAdapter, view, position, id->
            exampleViewModel.categoryQuestion(dataAdapter.getItemAtPosition(position).toString()) // store the category uid list in viewmodel
            (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_nav_example_to_nav_question)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}