package com.noatnoat.pianoapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.noatnoat.pianoapp.databinding.FragmentHome2Binding

class HomeFragment2 : Fragment() {
    private var _binding: FragmentHome2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         binding.tvPiano.text = "1. Piano"
         binding.tvGuitar.text = "2. Guitar"
         binding.tvDrumset.text = "3. Drumset"
         binding.tvSaxophone.text = "4. Saxophone"
         binding.tvRecordList.text = "5. Record list"

        binding.tvPiano.setOnClickListener {
            val action = HomeFragment2Directions.actionHomeFragment2ToPianoFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}