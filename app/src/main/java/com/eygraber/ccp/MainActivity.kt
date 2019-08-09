package com.eygraber.ccp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    EmojiCompat.get().registerInitCallback(object : EmojiCompat.InitCallback() {
      override fun onInitialized() {
        super.onInitialized()

        setContentView(R.layout.activity_main)
      }
    })
  }
}
