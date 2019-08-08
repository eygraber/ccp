package com.eygraber.ccp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class CountryCodePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
  private val country: TextView
  private val arrow: ImageView
  private val tapTarget: View

  private val ccpAttrs: CcpAttrs

  private var clickListener: OnClickListener? = null

  private val internalClickListener = OnClickListener {
    if(isClickable) {
      clickListener?.onClick(it) ?: run {

      }
    }
  }

  init {
    if(childCount == 0) {
      val layout = if(attrs.isWidthMatchParent()) {
        R.layout.code_picker_full_width
      }
      else {
        R.layout.code_picker
      }

      LayoutInflater.from(context).inflate(layout, this, true)
    }

    country = findViewById(R.id.country)
    arrow = findViewById(R.id.arrow)
    tapTarget = findViewById(R.id.tapTarget)
    tapTarget.setOnClickListener(internalClickListener)

    ccpAttrs = CcpAttrs.fromAttrs(context, attrs)
  }

  override fun setOnClickListener(listener: OnClickListener) {
    clickListener = listener
  }
}

private fun AttributeSet?.isWidthMatchParent() =
    this?.run {
      getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width")?.let { xmlWidth ->
        Integer.valueOf(xmlWidth) == ConstraintLayout.LayoutParams.MATCH_PARENT ||
            "fill_parent" == xmlWidth || "match_parent" == xmlWidth
      }
    } ?: false

private fun AttributeSet?.isClickable() =
    this?.run {
      getAttributeValue("http://schemas.android.com/apk/res/android", "is_clickable")?.toBoolean()
    } ?: false
