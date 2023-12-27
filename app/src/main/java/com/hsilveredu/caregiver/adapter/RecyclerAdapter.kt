package com.hsilveredu.caregiver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hsilveredu.caregiver.R

class RecyclerAdapter(val data: List<Int>): RecyclerView.Adapter<RecyclerAdapter.HorizontalViewHolder>() {
    class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.item_textview)
    }

    interface onItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_tv, parent, false)
        return HorizontalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.toString()
        holder.itemView.setOnClickListener {
            m_listener.onItemClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setItemClickListener(onItemClickListener: onItemClickListener) {
        this.m_listener = onItemClickListener
    }

    private lateinit var m_listener: onItemClickListener
}