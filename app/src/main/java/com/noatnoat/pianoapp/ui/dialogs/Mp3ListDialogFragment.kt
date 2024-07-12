package com.noatnoat.pianoapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.noatnoat.pianoapp.databinding.DialogMp3ListBinding
import com.noatnoat.pianoapp.helpers.piano.AudioFileHelper
import com.noatnoat.pianoapp.helpers.piano.MusicPlayerHelper
import com.noatnoat.pianoapp.ui.adapters.Mp3Adapter
import com.noatnoat.pianoapp.ui.base.BaseDialogFragment

class Mp3ListDialogFragment : BaseDialogFragment() {

    private lateinit var binding: DialogMp3ListBinding
    private lateinit var musicPlayerHelper: MusicPlayerHelper

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        binding = DialogMp3ListBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        musicPlayerHelper = MusicPlayerHelper(requireContext())

        val audioFileHelper = AudioFileHelper(requireContext())
        val mp3Files = audioFileHelper.getAllMusic()
        val adapter = Mp3Adapter(mp3Files) { path ->
            musicPlayerHelper.playMusicFromPath(path)
        }
        binding.mp3RecyclerView.layoutManager = LinearLayoutManager(context)
        binding.mp3RecyclerView.adapter = adapter

        binding.closePopupBtn.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        musicPlayerHelper.release() // Ensure resources are released when the dialog is destroyed
    }
}