package com.noatnoat.pianoapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noatnoat.pianoapp.R

class SongListPianoAdapter(private val songs: Array<String>) : RecyclerView.Adapter<SongListPianoAdapter.SongViewHolder>() {
    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.songTitleTextView.text = songs[position]
    }

    override fun getItemCount() = songs.size

    class SongViewHolder(itemView: View, private val onItemClickListener: ((String) -> Unit)?) : RecyclerView.ViewHolder(itemView) {
        val songTitleTextView: TextView = itemView.findViewById(R.id.songTitleTextView)

        init {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(songTitleTextView.text.toString())
            }
        }
    }
}