package com.noatnoat.pianoapp.admob.helper.appoppen.params

/**
 * Created by ViO on 16/03/2024.
 */
open class AdAppOpenState {
    data object None : AdAppOpenState()
    data object Fail : AdAppOpenState()
    data object Loading : AdAppOpenState()
    data object Loaded : AdAppOpenState()
    data object ShowFail : AdAppOpenState()
    data object Showed : AdAppOpenState()
    data object Cancel : AdAppOpenState()
}