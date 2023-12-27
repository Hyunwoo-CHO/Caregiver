package com.hsilveredu.caregiver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.hsilveredu.caregiver.R

class CategoryAdapter (val context: Context, val data_list: ArrayList<String>) : BaseAdapter(){
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.category_listview, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val dataName = view.findViewById<TextView>(R.id.data_name)
        val dataImg = view.findViewById<ImageFilterView>(R.id.symbol)

        val data = data_list[position]
        dataName.text = data
        dataImg.setImageResource(R.drawable.list)

        return view
    }

    override fun getItem(position: Int): Any {
        return data_list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return data_list.size
    }
}