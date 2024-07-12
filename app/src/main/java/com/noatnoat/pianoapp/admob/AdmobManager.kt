package com.noatnoat.pianoapp.admob

object AdmobManager {
    var isShowAdsFullScreen = false
        private set
    var isAdsClicked = false
    fun adsClicked(){
        isAdsClicked = true
    }

    fun adsClickedInValid(){
        isAdsClicked = false
    }

    fun adsShowFullScreen() {
        isShowAdsFullScreen = true
    }

    fun adsFullScreenDismiss() {
        isShowAdsFullScreen = false
    }
}