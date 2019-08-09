@file:Suppress("SetTextI18n")

package com.eygraber.ccp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

internal class CountryAdapter(
  private val countryCodePicker: CountryCodePicker,
  private val countries: List<Country>,
  private val emptyView: View,
  private val recyclerViewGroup: Group
) : ListAdapter<Country, VH>(diffCallback) {
  init {
    submitList(countries)
  }

  private val emoji: EmojiCompat? = if(countryCodePicker.ccpAttrs.useEmojiCompat) EmojiCompat.get() else null

  private val clickListener = View.OnClickListener { view ->
    val position = view.getTag(R.id.ccp_selected_country_position) as Int
    countryCodePicker.onCountrySelected(getItem(position))
  }

  override fun getItemId(position: Int): Long = getItem(position).name.toLong()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
    VH(LayoutInflater.from(parent.context).inflate(R.layout.dialog_country_item, parent, false))

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.itemView.setTag(R.id.ccp_selected_country_position, position)
    holder.itemView.setOnClickListener(clickListener)

    val country = getItem(position)
    val name = holder.itemView.context.getString(country.name)
    val flag = emoji?.process(country.flag) ?: country.flag

    holder.country.text = "$flag $name"
    holder.code.text = "+${country.callingCode}"
    holder.divider.isVisible = position != itemCount - 1
  }

  fun updateSearch(search: CharSequence) {
    if(search.isBlank()) {
      emptyView.isVisible = false
      recyclerViewGroup.isVisible = true
      submitList(countries)
      return
    }

    val normalizedSearch = search.stripAccents()

    val filteredList = countries
      .filter { country ->
        val name = countryCodePicker.context.getString(country.name).stripAccents()
        val names = name.split(" ")
        names.any {
          it.startsWith(normalizedSearch, ignoreCase = true)
        }
      }

    submitList(filteredList)
  }

  override fun onCurrentListChanged(previousList: MutableList<Country>, currentList: MutableList<Country>) {
    super.onCurrentListChanged(previousList, currentList)

    recyclerViewGroup.isVisible = currentList.isNotEmpty()
    emptyView.isVisible = currentList.isEmpty()
  }

  companion object {
    val diffCallback = object : DiffUtil.ItemCallback<Country>() {
      override fun areItemsTheSame(oldItem: Country, newItem: Country) = oldItem == newItem

      override fun areContentsTheSame(oldItem: Country, newItem: Country) = false
    }
  }
}

internal class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
  val country: TextView = itemView.findViewById(R.id.country)
  val code: TextView = itemView.findViewById(R.id.code)
  val divider: View = itemView.findViewById(R.id.divider)
}