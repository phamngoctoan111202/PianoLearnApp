package com.noatnoat.pianoapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentRecordBinding
import com.noatnoat.pianoapp.helpers.AudioRecorderHelper
import com.noatnoat.pianoapp.ui.adapters.SongRecordListAdapter

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val audioRecorderHelper = AudioRecorderHelper(requireContext(), TextView(requireContext())) // Update TextView with actual reference if needed
        val records = audioRecorderHelper.getRecordList()

        val adapter = SongRecordListAdapter(records)
        binding.recyclerViewRecord.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRecord.adapter = adapter

        if (records.isEmpty()) {
            binding.emptyRecordImg.visibility = View.VISIBLE
            binding.emptyRecordTxt.visibility = View.VISIBLE
            binding.recyclerViewRecord.visibility = View.GONE
        } else {
            binding.emptyRecordImg.visibility = View.GONE
            binding.emptyRecordTxt.visibility = View.GONE
            binding.recyclerViewRecord.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}