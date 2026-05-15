package com.partxis.clasificacion.data

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun setLocale(languageCode: String): Context {
        val locale = when (languageCode) {
            "es" -> Locale("es")
            "en" -> Locale("en")
            else -> Locale("eu")  // Default to Basque
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }

    fun getLocaleFromCode(code: String): Locale {
        return when (code) {
            "es" -> Locale("es")
            "en" -> Locale("en")
            else -> Locale("eu")
        }
    }
}