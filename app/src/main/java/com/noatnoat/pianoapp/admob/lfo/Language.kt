package com.noatnoat.pianoapp.admob.lfo

data class Language(
    var code: String,
    var internationalName: String,
    var nationalName: String,
    var idIcon: Int = 0,
    var isChoose: Boolean = false,
    var isSystem: Boolean = false
)
