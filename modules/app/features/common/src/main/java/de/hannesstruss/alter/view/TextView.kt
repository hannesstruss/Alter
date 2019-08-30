package de.hannesstruss.alter.view

import android.widget.TextView

var TextView.textIfChanged: String
  get() = text.toString()
  set(value) {
    if (value != text.toString()) {
      text = value
    }
  }
