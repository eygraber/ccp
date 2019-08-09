package com.eygraber.ccp

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.core.os.ConfigurationCompat
import java.util.*

internal data class CcpAttrs(
  val defaultCountry: Country,
  val useEmojiCompat: Boolean,
  val showFlag: Boolean,
  val showCallingCode: Boolean,
  val showArrow: Boolean,
  val dialogShowTitle: Boolean,
  val dialogTitle: String,
  val dialogShowSearch: Boolean,
  val dialogSearchHint: String,
  val dialogShowFastScroller: Boolean,
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
            defaultCountry = (a.getString(R.styleable.CountryCodePicker_ccp_default_country)
              ?.toLowerCase(Locale.US)
              ?.takeIf { it in Country.countries }
              ?: defaultCountry)
              .let { countryCode ->
                Country.countries.getValue(countryCode)
              },

            useEmojiCompat = a.getBoolean(R.styleable.CountryCodePicker_ccp_use_emoji_compat, false),

            showFlag = a.getBoolean(R.styleable.CountryCodePicker_ccp_show_flag, true),

            showCallingCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_show_calling_code, true),

            showArrow = a.getBoolean(R.styleable.CountryCodePicker_ccp_show_arrow, true),

            dialogShowTitle = a.getBoolean(R.styleable.CountryCodePicker_ccp_dialog_show_title, true),

            dialogTitle = a.getString(R.styleable.CountryCodePicker_ccp_dialog_title)
              ?: context.defaultDialogTitle,

            dialogShowSearch = a.getBoolean(R.styleable.CountryCodePicker_ccp_dialog_show_search, true),

            dialogSearchHint = a.getString(R.styleable.CountryCodePicker_ccp_dialog_search_hint)
              ?: context.defaultDialogSearchHint,

            dialogShowFastScroller = a.getBoolean(R.styleable.CountryCodePicker_ccp_dialog_show_fast_scroller, true),

            dialogEmptyViewText = a.getString(R.styleable.CountryCodePicker_ccp_dialog_empty_view_text)
              ?: context.defaultDialogEmptyViewText,

            dialogPriorityCountries = a.getString(R.styleable.CountryCodePicker_ccp_dialog_priority_countries).csvToSet(),

            dialogIncludeCountries = a.getString(R.styleable.CountryCodePicker_ccp_dialog_include_countries).csvToSet(),

            dialogExcludeCountries = a.getString(R.styleable.CountryCodePicker_ccp_dialog_exclude_countries).csvToSet()
          )
        }
      }
        ?: CcpAttrs(
          defaultCountry = Country.countries.getValue(context.defaultCountry),
          useEmojiCompat = false,
          showFlag = true,
          showCallingCode = true,
          showArrow = true,
          dialogShowTitle = true,
          dialogTitle = context.defaultDialogTitle,
          dialogShowSearch = true,
          dialogSearchHint = context.defaultDialogSearchHint,
          dialogShowFastScroller = true,
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

private val Context.defaultCountry
  get() =
    ConfigurationCompat
      .getLocales(resources.configuration)
      .get(0)
      .country
      .toLowerCase(Locale.US)

private fun String?.csvToSet() =
  this
    ?.split(",")
    ?.map { it.toLowerCase(Locale.US) }
    ?.filter { it in Country.countries }
    ?.toSet()
    ?: emptySet()

