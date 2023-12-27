package com.hsilveredu.caregiver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.hsilveredu.caregiver.R

class ListviewAdapter (val context: Context, val data_list: ArrayList<String>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view : View
        val holder : CustomViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.data_listview, null)
            holder = CustomViewHolder()
            holder.image = view.findViewById(R.id.symbol)
            holder.dataname = view.findViewById(R.id.data_name)
            holder.datanum = view.findViewById(R.id.data_num)

            view.tag = holder
        } else {
            holder = convertView.tag as CustomViewHolder
            view = convertView
        }

        val data = data_list[position]
        holder.dataname?.text = data
        holder.datanum?.text = (position + 1).toString() + "."


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

class CustomViewHolder {
    var image: ImageFilterView? = null
    var dataname: TextView? = null
    var datanum: TextView? = null
}