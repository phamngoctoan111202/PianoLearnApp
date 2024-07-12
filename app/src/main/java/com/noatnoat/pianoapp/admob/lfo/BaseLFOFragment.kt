package com.noatnoat.pianoapp.admob.lfo

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentLfoBinding
import com.noatnoat.pianoapp.ui.base.BaseFragment
import com.noatnoat.pianoapp.utils.Helper

/**
 * Created by ViO on 17/03/2024.
 */

abstract class BaseLFOFragment : BaseFragment() {

    val KEY_SELECT_POSITION = "KEY_SELECT_POSITION"
    val KEY_SCROLL_Y = "KEY_SCROLL_Y"

    protected var binding: FragmentLfoBinding? = null
    private var myContext: Context? = null
    protected var myActivity: Activity? = null
    protected val lfoAdapter by lazy {
        myContext?.let {
            LFOAdapter(
                it,
                6,
                LanguageUtils.getListLanguage()
            )
        }
    }


    protected abstract fun initView()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
        myActivity = myContext as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLfoBinding.inflate(layoutInflater)
        initView()
        handleBack()
        super.onCreateView(inflater, container, savedInstanceState)
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity?.let {
            FirebaseAnalytics.getInstance(it)
                .logEvent(this::class.simpleName.toString(), Bundle())
        }
        setStatusBarColor(R.drawable.gradient_theme)
    }


    protected fun navigateToSelect(languagePosition: Int, scrollY: Int) {
        if (!Helper.isDoubleClick()) {
            val bundle = Bundle()
            bundle.putInt(KEY_SELECT_POSITION, languagePosition)
            bundle.putInt(KEY_SCROLL_Y, scrollY)

            findNavController().navigate(R.id.actionLfoToLFOSelect, bundle)
        }
    }

    private fun handleBack() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    myActivity?.finish()
                }
            })
    }
}
