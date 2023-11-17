package com.hyun.caregiver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.hyun.caregiver.R

class ListviewAdapter (val context: Context, val data_list: ArrayList<String>) : BaseAdapter(){
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.data_listview, null)

        val dataName = view.findViewById<TextView>(R.id.data_name)
        //val img = view.findViewById<ImageFilterView>(R.id.symbol)

        val data = data_list[position]
        dataName.text = data
        //img.alpha = 0f

        return view
    }

    override fun getItem(position: Int): Any {
        return data_list.indexOf(data_list[position])
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return data_list.size
    }
}