package de.hannesstruss.alter.domain

import android.content.Context
import android.text.style.TextAppearanceSpan
import androidx.annotation.StyleRes
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import de.hannesstruss.alter.dates.Age
import de.hannesstruss.alter.features.common.R

private const val NO_TEXT_APPEARANCE = 0

fun Age.format(
  context: Context,
  @StyleRes numberTextAppearance: Int = NO_TEXT_APPEARANCE
): CharSequence {
  return buildSpannedString {
    if (numberTextAppearance != NO_TEXT_APPEARANCE) {
      inSpans(TextAppearanceSpan(context, numberTextAppearance)) {
        append(significantValue.toString())
      }
    } else {
      append(significantValue.toString())
    }
    append(" ")
    append(context.resources.getQuantityText(significantUnit, significantValue))

//    val includeLessSignificantValue = lessSignificantValueMode == LessSignificantValueMode.Always ||
//        (lessSignificantValueMode == LessSignificantValueMode.IfNonZero && lessSignificantValue != 0)
//    if (includeLessSignificantValue) {
//      append(" ")
//      if (numberTextAppearance != NO_TEXT_APPEARANCE) {
//        inSpans(TextAppearanceSpan(context, numberTextAppearance)) {
//          append(lessSignificantValue.toString())
//        }
//      } else {
//        append(lessSignificantValue.toString())
//      }
//      append(" ")
//      append(context.resources.getQuantityText(lessSignificantUnit, lessSignificantValue))
//    }
  }
}

private val Age.significantValue: Int get() = when (this) {
  is Age.Days -> days
  is Age.Weeks -> weeks
  is Age.Months -> months
  is Age.Years -> years
}

private val Age.significantUnit
  get() = when (this) {
    is Age.Days -> R.plurals.age_unit_days
    is Age.Weeks -> R.plurals.age_unit_weeks
    is Age.Months -> R.plurals.age_unit_months
    is Age.Years -> R.plurals.age_unit_years
  }
