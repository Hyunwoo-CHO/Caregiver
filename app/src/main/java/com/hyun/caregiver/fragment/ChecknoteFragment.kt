package com.hyun.caregiver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.hyun.caregiver.adapter.CategoryAdapter
import com.hyun.caregiver.MainActivity
import com.hyun.caregiver.MainViewModel
import com.hyun.caregiver.MainViewModelFactory
import com.hyun.caregiver.R
import com.hyun.caregiver.databinding.FragmentChecknoteBinding

class ChecknoteFragment : Fragment() {

    private var _binding: FragmentChecknoteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var pastdataAdapter : CategoryAdapter
    private lateinit var exampledataAdapter : CategoryAdapter
    private var examplePress = false
    private var pastPress = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val checknoteViewModel =
            ViewModelProvider((activity as MainActivity), MainViewModelFactory((activity as MainActivity).application)).get(MainViewModel::class.java)

        _binding = FragmentChecknoteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        checknoteViewModel.pastlist.observe(viewLifecycleOwner) {
            val data_list = ArrayList<String>()
            data_list.addAll(it)
            pastdataAdapter = CategoryAdapter((activity as MainActivity), data_list)
            binding.pastList.adapter = pastdataAdapter
        }

        checknoteViewModel.examplelist.observe(viewLifecycleOwner) {
            val data_list = ArrayList<String>()
            data_list.addAll(it)
            exampledataAdapter = CategoryAdapter((activity as MainActivity), data_list)
            binding.exampleList.adapter = exampledataAdapter
        }

        binding.pastquestionChecknote.setOnClickListener {
            if (!pastPress) {
                checknoteViewModel.getChecknoteCategory("모의고사")
                binding.pastList.isVisible = true
            } else {
                binding.pastList.isVisible = false
            }
            pastPress = !pastPress
        }

        binding.exampleChecknote.setOnClickListener {
            if (!examplePress) {
                checknoteViewModel.getChecknoteCategory("예상문제")
                binding.exampleList.isVisible = true
            } else {
                binding.exampleList.isVisible = false
            }
            examplePress = !examplePress
        }

        binding.pastList.setOnItemClickListener { dataAdapter, view, position, id ->
            checknoteViewModel.checknoteQuestion(dataAdapter.getItemAtPosition(position).toString())
            (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(
                R.id.action_nav_checknote_to_nav_wrong)
        }

        binding.exampleList.setOnItemClickListener { dataAdapter, view, position, id ->
            checknoteViewModel.checknoteQuestion(dataAdapter.getItemAtPosition(position).toString())
            (activity as MainActivity).findNavController(R.id.nav_host_fragment_content_main).navigate(
                R.id.action_nav_checknote_to_nav_wrong)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}