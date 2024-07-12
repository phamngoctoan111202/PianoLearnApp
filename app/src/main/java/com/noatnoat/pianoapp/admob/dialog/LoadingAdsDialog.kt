package com.noatnoat.pianoapp.admob.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.noatnoat.pianoapp.R

/**
 * Created by ViO on 16/03/2024.
 */
class LoadingAdsDialog(context: Context) : Dialog(context, R.style.Dialog_FullScreen_Light) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading_ads)
    }
}