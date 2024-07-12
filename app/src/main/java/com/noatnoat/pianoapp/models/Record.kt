package com.noatnoat.pianoapp.models

import com.noatnoat.pianoapp.enums.MusicalInstrument

data class Record(
    val name: String,
    val duration: String,
    val date: String,
    val path: String,
    val instrument: MusicalInstrument // Add this line
)