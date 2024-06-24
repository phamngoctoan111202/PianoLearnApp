package com.noatnoat.pianoapp.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {

    val loader = MutableLiveData<Boolean>(false)

    init {
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            loader.value = true
        }
    }


}