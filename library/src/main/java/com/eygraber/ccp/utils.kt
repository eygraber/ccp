package com.eygraber.ccp

import java.text.Normalizer

internal fun CharSequence.stripAccents(): String {
  val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
  return normalized.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
}
