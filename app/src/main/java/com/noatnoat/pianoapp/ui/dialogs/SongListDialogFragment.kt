package com.noatnoat.pianoapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.ui.adapters.SongListPianoAdapter
import com.noatnoat.pianoapp.ui.base.BaseDialogFragment
import com.noatnoat.pianoapp.ui.callbacks.OnSongSelectedListener

class SongListDialogFragment(private val songs: Array<String>, private val listener: OnSongSelectedListener) : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            // Inflate and set the custom title for the dialog
            val titleView = inflater.inflate(R.layout.dialog_title_bar, null)
            builder.setCustomTitle(titleView)

            // Inflate the layout for the dialog's content
            val dialogLayout = inflater.inflate(R.layout.dialog_song_list, null)
            val recyclerView = dialogLayout.findViewById<RecyclerView>(R.id.recyclerView2)
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            // Set up the adapter for the RecyclerView
            val adapter = SongListPianoAdapter(songs).apply {
                setOnItemClickListener { songName ->
                    listener.onSongSelected(songName)
                    dismiss() // Dismiss the dialog when an item is clicked
                }
            }
            recyclerView.adapter = adapter

            // Set the dialog's view to the inflated layout
            builder.setView(dialogLayout)

            // Set up the close button's click listener to dismiss the dialog
            titleView.findViewById<ImageButton>(R.id.closeButton).setOnClickListener {
                dismiss()
            }

            // Create and return the AlertDialog
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.bgr_corner)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}