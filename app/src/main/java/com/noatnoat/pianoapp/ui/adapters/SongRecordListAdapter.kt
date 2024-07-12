package com.noatnoat.pianoapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.models.Record

class SongRecordListAdapter(private var records: List<Record>) : RecyclerView.Adapter<SongRecordListAdapter.RecordViewHolder>() {

    var onItemClick: ((Record) -> Unit)? = null

    fun setRecords(newRecords: List<Record>) {
        records = newRecords
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record)
    }

    override fun getItemCount(): Int = records.size

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recordNameTextView: TextView = itemView.findViewById(R.id.record_name)
        private val recordDurationTextView: TextView = itemView.findViewById(R.id.record_duration)
        private val recordMoreImageView: ImageView = itemView.findViewById(R.id.record_more)
        private var recordPath: String = ""

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(records[adapterPosition])
                // Here you can use recordPath for something, like playing the music
            }
            recordMoreImageView.setOnClickListener {
                // Handle more options click event here, possibly using recordPath
            }
        }

        fun bind(record: Record) {
            recordNameTextView.text = record.name
            recordDurationTextView.text = record.duration
            recordPath = record.path // Store the path for use in click listeners or elsewhere
        }
    }
}