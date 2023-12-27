package com.hsilveredu.caregiver.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hsilveredu.caregiver.databinding.DialogConfirmBinding
import com.hsilveredu.caregiver.databinding.DialogFinishBinding

class FinishDialog( text: String ) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogFinishBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemClickListener: ItemClickListener
    private var text: String? = null
    init {
        this.text = text
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFinishBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.confirmTextView.text = text

        // 확인 버튼 클릭
        binding.yesButton.setOnClickListener {
            itemClickListener.onClick()
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface ItemClickListener {
        fun onClick()
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}