package com.eygraber.ccp.sample

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        EmojiCompat.init(
                FontRequestEmojiCompatConfig(
                        applicationContext,
                        FontRequest(
                                "com.google.android.gms.fonts",
                                "com.google.android.gms",
                                "Noto Color Emoji Compat",
                                R.array.com_google_android_gms_fonts_certs
                        )
                ).apply {
                    setReplaceAll(true)
                }
        )
    }
}
