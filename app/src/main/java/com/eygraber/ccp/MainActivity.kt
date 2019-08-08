package com.eygraber.ccp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.dialog_picker)

    val r = findViewById<RecyclerView>(R.id.countryList)
    r.layoutManager = LinearLayoutManager(this)
    r.adapter = Adapter()
  }
}

class Adapter : RecyclerView.Adapter<VH>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutInflater.from(parent.context).inflate(R.layout.dialog_country_item, parent, false))
  }

  override fun getItemCount(): Int {
    return Country.countries.size
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    val country = Country.countries.values.toList()[position]
    val name = holder.itemView.context.getString(country.name)
    holder.country.text = "${country.flag} ${name}"
    holder.code.text = "+${country.callingCode}"
  }
}

class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
  val country: TextView = itemView.findViewById(R.id.country)
  val code: TextView = itemView.findViewById(R.id.code)
}
