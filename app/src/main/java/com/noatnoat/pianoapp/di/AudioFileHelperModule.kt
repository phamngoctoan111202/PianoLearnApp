package com.noatnoat.pianoapp.di

import android.content.Context
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object AudioFileHelperModule {
//    @Singleton
//    @Provides
//    fun provideAudioFileHelper(@ApplicationContext context: Context): List<String> {
//        return AudioFileHelper.getAllAudioFromDevice(context)
//    }
//}