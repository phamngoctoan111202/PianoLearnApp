package com.noatnoat.pianoapp.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Mp3Adapter(private val mp3Files: List<String>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<Mp3Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Hiển thị tên file mp3 thay vì đường dẫn đầy đủ
        holder.textView.text = mp3Files[position].substringAfterLast('/')
        holder.itemView.setOnClickListener {
            // Gửi đường dẫn đầy đủ khi một item được click
            onItemClick(mp3Files[position])
        }
    }

    override fun getItemCount() = mp3Files.size
}