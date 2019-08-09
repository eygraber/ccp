package com.eygraber.ccp

import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat

class SampleApp : Application() {
  override fun onCreate() {
    super.onCreate()

    EmojiCompat.init(BundledEmojiCompatConfig(applicationContext))
  }
}
