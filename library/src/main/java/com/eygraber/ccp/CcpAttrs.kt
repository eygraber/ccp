package com.eygraber.ccp

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.core.os.ConfigurationCompat

data class CcpAttrs(
  val defaultCountry: String,
  val dialogTitle: String,
  val dialogSearchHint: String,
  val dialogEmptyViewText: String,
  val dialogPriorityCountries: Set<String>,
  val dialogIncludeCountries: Set<String>,
  val dialogExcludeCountries: Set<String>
) {
  companion object {
    fun fromAttrs(context: Context, attrs: AttributeSet?) = with(context) {
      attrs?.let { attributes ->
        theme.obtainStyledAttributes(attributes, R.styleable.CountryCodePicker, 0, 0).use { a ->
          CcpAttrs(
            defaultCountry = a.getString(R.styleable.CountryCodePicker_ccp_default_country)
              ?.takeIf { it in Country.countries }
              ?: defaultLocale.country,

            dialogTitle = a.getString(R.styleable.CountryCodePicker_ccp_dialog_title)
              ?: context.defaultDialogTitle,

            dialogSearchHint = a.getString(R.styleable.CountryCodePicker_ccp_dialog_search_hint)
              ?: context.defaultDialogSearchHint,

            dialogEmptyViewText = a.getString(R.styleable.CountryCodePicker_ccp_dialog_empty_view_text)
              ?: context.defaultDialogEmptyViewText,

            dialogPriorityCountries = a.getString(R.styleable.CountryCodePicker_ccp_dialog_priority_countries).csvToSet(),

            dialogIncludeCountries = a.getString(R.styleable.CountryCodePicker_ccp_dialog_include_countries).csvToSet(),

            dialogExcludeCountries = a.getString(R.styleable.CountryCodePicker_ccp_dialog_exclude_countries).csvToSet()
          )
        }
      }
        ?: CcpAttrs(
          defaultCountry = context.defaultLocale.country,
          dialogTitle = context.defaultDialogTitle,
          dialogSearchHint = context.defaultDialogSearchHint,
          dialogEmptyViewText = context.defaultDialogEmptyViewText,
          dialogPriorityCountries = emptySet(),
          dialogIncludeCountries = emptySet(),
          dialogExcludeCountries = emptySet()
        )
    }
  }
}

private val Context.defaultDialogTitle get() = getString(R.string.default_dialog_title)
private val Context.defaultDialogSearchHint get() = getString(R.string.default_dialog_search_hint)
private val Context.defaultDialogEmptyViewText get() = getString(R.string.default_dialog_empty_view_text)

private val Context.defaultLocale get() = ConfigurationCompat.getLocales(resources.configuration).get(0)

private fun String?.csvToSet() =
  this
    ?.split(",")
    ?.filter { it in Country.countries }
    ?.toSet()
    ?: emptySet()

