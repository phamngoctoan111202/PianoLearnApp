package com.noatnoat.pianoapp.admob.lfo

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.SharedPreferencesManager
import dev.b3nedikt.app_locale.AppLocale
import java.util.Locale
/**
 * Created by ViO on 17/03/2024.
 */
object LanguageUtils {
    fun getListLanguage(): List<Language> {
        val languages: MutableList<Language> = arrayListOf()
        languages.add(Language("en", "English", "English", R.drawable.flag_en, false))
        languages.add(Language("hi", "Hindi", "Hindi", R.drawable.flag_in, false))
        languages.add(Language("es", "Spanish", "Spanish", R.drawable.flag_sp, false))
        languages.add(Language("fr", "French", "French", R.drawable.flag_fr, false))
        languages.add(Language("hi", "Hindi", "Hindi", R.drawable.flag_in, false))
        languages.add(Language("ru", "Russian", "Russian", R.drawable.flag_ru, false))
        languages.add(Language("pt", "Portuguese", "Portuguese", R.drawable.flag_po, false))
        languages.add(Language("bn", "Bengal", "Bengal", R.drawable.flag_be, false))
        languages.add(Language("de", "German", "German", R.drawable.flag_de, false))
        languages.add(Language("ja", "Japanese", "Japanese", R.drawable.flag_jp, false))
//        languages.add(Language("ko", "Korean", "Korean", R.drawable.flag_ko, false))

//        languages.add(Language("it", "Italian", "Italian", R.drawable.flag_it, false))
//        languages.add(Language("in", "Indonesian", "Indonesian", R.drawable.flag_id, false))
        return languages.getHandleListLanguage("en", 3)
    }

    private fun getLanguageDevice(languageList: MutableList<Language>): Language? {
        val locale =
            Resources.getSystem().configuration.locales.get(0)
        return languageList.find { it.code == locale.language }
    }

    private fun MutableList<Language>.getHandleListLanguage(
        languageCodeDefault: String,
        positionLanguageDevice: Int
    ): List<Language> {
        val language = this.find { it.code == languageCodeDefault }
        language?.let {
            this.remove(it)
//            it.isChoose = true
            it.isChoose = false
            this.add(0, it)
        }
        getLanguageDevice(this)?.let {
            if (it.code != languageCodeDefault) {
                this.remove(it)
                this.add(positionLanguageDevice, it)
            }
            it.isSystem = true
        }
        return this
    }

    private var myLocale: Locale? = null

    // Lưu ngôn ngữ đã cài đặt
    private fun saveLocale(lang: String, context: Context?) {
        context?.let {
            SharedPreferencesManager.getInstance(context).saveString("selected_language", lang)
        }
    }

    fun loadLocale(context: Context) {
        val language: String =
            SharedPreferencesManager.getInstance(context).getString("selected_language")
        if (language == "") {
            val config = Configuration()
            val locale = Locale.getDefault()
            Locale.setDefault(locale)
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        } else {
            changeLang(language, context)
        }
    }

    // method phục vụ cho việc thay đổi ngôn ngữ.
    fun changeLang(lang: String, context: Context) {
        if (lang.equals("", ignoreCase = true)) return
        myLocale = Locale(lang)
        saveLocale(lang, context)
        myLocale?.let {
            Locale.setDefault(it)
            AppLocale.desiredLocale = it
        }
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

}

