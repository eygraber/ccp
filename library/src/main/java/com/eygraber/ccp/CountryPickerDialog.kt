package com.eygraber.ccp

import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerThumbView
import com.reddit.indicatorfastscroll.FastScrollerView
import java.util.*

internal class CountryPickerDialog(
  countryCodePicker: CountryCodePicker,
  countries: List<Country>
) {
  private val context = countryCodePicker.context
  private val ccpAttrs = countryCodePicker.ccpAttrs

  private val dialog: AlertDialog =
    AlertDialog
      .Builder(context).apply {
        setCancelable(true)
        setView(R.layout.dialog_picker)
      }.show()

  private val titleView = dialog.findViewById<TextView>(R.id.title)!!
  private val searchView = dialog.findViewById<EditText>(R.id.search)!!
  private val clearSearchView = dialog.findViewById<View>(R.id.clearSearch)!!
  private val recyclerView = dialog.findViewById<RecyclerView>(R.id.countryList)!!
  private val fastScrollerView = dialog.findViewById<FastScrollerView>(R.id.fastscroller)!!
  private val fastScrollerThumbView = dialog.findViewById<FastScrollerThumbView>(R.id.fastscrollerThumb)!!
  private val emptyView = dialog.findViewById<TextView>(R.id.emptyResults)!!

  private val parentView = titleView.parent as ViewGroup

  private val countryAdapter = CountryAdapter(countryCodePicker, countries, ccpAttrs) { newList ->
    recyclerView.isVisible = newList.isNotEmpty()
    fastScrollerView.isVisible = newList.isNotEmpty() && ccpAttrs.dialogShowFastScroller
    fastScrollerThumbView.isVisible = newList.isNotEmpty() && ccpAttrs.dialogShowFastScroller

    emptyView.isVisible = newList.isEmpty()
  }.apply {
    setHasStableIds(true)
  }

  private val clearSearchTransition = Fade().apply {
    addTarget(clearSearchView)
  }

  private val cancelRunnable = CancelSearchRunnable()

  init {
    ccpAttrs.dialogFastScrollTextAppearance?.let { res ->
      fastScrollerView.textAppearanceRes = res
    }

    with(titleView) {
      isVisible = ccpAttrs.dialogShowTitle
      text = ccpAttrs.dialogTitle
    }

    with(searchView) {
      isVisible = ccpAttrs.dialogShowSearch
      hint = ccpAttrs.dialogSearchHint

      if(ccpAttrs.dialogShowSearch) {
        observeSearch()
      }
    }

    clearSearchView.setOnClickListener {
      searchView.setText("")
    }

    with(recyclerView) {
      post {
        layoutManager = LinearLayoutManager(context)
        adapter = countryAdapter

        if(ccpAttrs.dialogShowFastScroller) {
          fastScrollerView.setupWithRecyclerView(
            recyclerView,
            getItemIndicator = { position ->
              if(searchView.text.isNotEmpty()) {
                null
              } else if(countries[position].priority) {
                FastScrollItemIndicator.Icon(R.drawable.ccp_ic_priority)
              } else {
                val item = countries[position]
                val name = context.getString(item.name)
                FastScrollItemIndicator.Text(
                  name.substring(0, 1).stripAccents().toUpperCase(Locale.getDefault())
                )
              }
            },
            useDefaultScroller = false
          )

          fastScrollerThumbView.setupWithFastScroller(fastScrollerView)

          fastScrollerView.itemIndicatorSelectedCallbacks += object : FastScrollerView.ItemIndicatorSelectedCallback {
            override fun onItemIndicatorSelected(
              indicator: FastScrollItemIndicator,
              indicatorCenterY: Int,
              itemPosition: Int
            ) {
              recyclerView.smoothSnapToPosition(itemPosition)
            }
          }
        } else {
          fastScrollerView.isVisible = false
          fastScrollerThumbView.isVisible = false
        }
      }
    }

    emptyView.text = ccpAttrs.dialogEmptyViewText
  }

  fun close() {
    dialog.cancel()
  }

  private fun EditText.observeSearch() {
    addTextChangedListener {
      removeCallbacks(cancelRunnable)

      val search = it?.trim() ?: ""

      val newClearSearchVisibility = search.isNotBlank()
      if(newClearSearchVisibility != clearSearchView.isVisible) {
        TransitionManager.beginDelayedTransition(parentView, clearSearchTransition)
        clearSearchView.isVisible = newClearSearchVisibility
      }

      postDelayed(
        SearchRunnable {
          countryAdapter.updateSearch(search)
        }, 500
      )
    }
  }

  private class SearchRunnable(
    private val updateSearch: () -> Unit
  ) : Runnable {
    override fun run() {
      updateSearch()
    }

    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is SearchRunnable || other is CancelSearchRunnable
  }

  private class CancelSearchRunnable : Runnable {
    override fun run() {}
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is SearchRunnable || other is CancelSearchRunnable
  }
}

private fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
  val smoothScroller = object : LinearSmoothScroller(this.context) {
    override fun getVerticalSnapPreference(): Int {
      return snapMode
    }

    override fun getHorizontalSnapPreference(): Int {
      return snapMode
    }
  }
  smoothScroller.targetPosition = position
  layoutManager?.startSmoothScroll(smoothScroller)
}
