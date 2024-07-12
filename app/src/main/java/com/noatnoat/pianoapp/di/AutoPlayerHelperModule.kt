package com.noatnoat.pianoapp.di

import android.content.Context
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.noatnoat.pianoapp.helpers.AudioRecorderHelper
import com.noatnoat.pianoapp.helpers.piano.PianoKeyPlayerHelper
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object AutoPlayerHelperModule {
//    @Singleton
//    @Provides
//    fun provideAutoPlayerHelper(pianoKeyPlayerHelper: PianoKeyPlayerHelper): AutoPlayerHelper {
//        return AutoPlayerHelper(pianoKeyPlayerHelper)
//    }
//}