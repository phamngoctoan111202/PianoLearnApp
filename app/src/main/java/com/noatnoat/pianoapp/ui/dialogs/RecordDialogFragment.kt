// Step 1 & 2: Modified RecordDialogFragment to include recording name functionality
package com.noatnoat.pianoapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.helpers.AudioRecorderHelper
import com.noatnoat.pianoapp.ui.base.BaseDialogFragment

class RecordDialogFragment(private val audioRecorderHelper: AudioRecorderHelper) : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val dialogLayout = inflater.inflate(R.layout.dialog_record_save, null)
            builder.setView(dialogLayout)

            val saveButton = dialogLayout.findViewById<Button>(R.id.button_save)
            val closeButton = dialogLayout.findViewById<Button>(R.id.button_close)
            val nameEditText = dialogLayout.findViewById<EditText>(R.id.subtitle_record)

            saveButton.setOnClickListener {
                val recordingName = nameEditText.text.toString()
                audioRecorderHelper.saveRecording()
                dismiss()
            }

            closeButton.setOnClickListener {
                dismiss()
            }

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.bgr_corner)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}