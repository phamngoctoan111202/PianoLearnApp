package com.noatnoat.pianoapp.admob.listener

import com.noatnoat.pianoapp.admob.data.ContentAd

/**
 * Created by ViO on 16/03/2024.
 */
interface AppOpenAdCallBack : ViOAdCallback<ContentAd.AdmobAd.ApAppOpenAd> {
    fun onAppOpenAdShow()
    fun onAppOpenAdClose()
}