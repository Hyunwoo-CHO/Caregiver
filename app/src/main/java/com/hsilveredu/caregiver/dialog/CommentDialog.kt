package com.hsilveredu.caregiver.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.hsilveredu.caregiver.R
import com.hsilveredu.caregiver.databinding.DialogCommentBinding

class CommentDialog( comment: String, comment_img: String ) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogCommentBinding? = null
    private val binding get() = _binding!!

    private var comment: String? = null
    private var comment_img: String? = null
    init {
        this.comment = comment
        this.comment_img = comment_img
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCommentBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.comment.text = comment
        if (!comment_img.equals("")) {
            Glide.with(this).load(comment_img).into(binding.commentImg)
            binding.commentImg.layoutParams.height = 300
        } else {
            binding.commentImg.layoutParams.height = 0
        }

        // 확인 버튼 클릭
        binding.yesButton.setOnClickListener {
            dismiss()
        }

        return view
    }

//    override fun getTheme(): Int {
//        return R.style.DialogTheme
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}