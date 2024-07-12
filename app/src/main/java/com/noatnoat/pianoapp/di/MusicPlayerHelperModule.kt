package com.noatnoat.pianoapp.di

import android.content.Context
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.noatnoat.pianoapp.helpers.piano.MusicPlayerHelper
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object MusicPlayerHelperModule {
//    @Singleton
//    @Provides
//    fun provideMusicPlayerHelper(@ApplicationContext context: Context): MusicPlayerHelper {
//        return MusicPlayerHelper(context)
//    }
//}