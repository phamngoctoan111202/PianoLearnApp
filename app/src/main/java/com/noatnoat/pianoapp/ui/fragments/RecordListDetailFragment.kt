package com.noatnoat.pianoapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentRecordDetailBinding
import com.noatnoat.pianoapp.ui.base.BaseFragment

class RecordListDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentRecordDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_detail, container, false)
        return binding.root
    }
}