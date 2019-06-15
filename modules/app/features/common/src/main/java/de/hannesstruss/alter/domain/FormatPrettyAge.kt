package de.hannesstruss.alter.domain

import android.content.Context
import android.text.style.TextAppearanceSpan
import androidx.annotation.StyleRes
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import de.hannesstruss.alter.features.common.R

private const val NO_TEXT_APPEARANCE = 0

enum class LessSignificantValueMode {
  Always, IfNonZero, Never
}

fun PrettyAge.format(
  context: Context,
  lessSignificantValueMode: LessSignificantValueMode = LessSignificantValueMode.Never,
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

    val includeLessSignificantValue = lessSignificantValueMode == LessSignificantValueMode.Always ||
        (lessSignificantValueMode == LessSignificantValueMode.IfNonZero && lessSignificantValue != 0)
    if (includeLessSignificantValue) {
      append(" ")
      if (numberTextAppearance != NO_TEXT_APPEARANCE) {
        inSpans(TextAppearanceSpan(context, numberTextAppearance)) {
          append(lessSignificantValue.toString())
        }
      } else {
        append(lessSignificantValue.toString())
      }
      append(" ")
      append(context.resources.getQuantityText(lessSignificantUnit, lessSignificantValue))
    }
  }
}

private val PrettyAge.significantUnit
  get() = when (this) {
    is PrettyAge.Days -> R.plurals.age_unit_days
    is PrettyAge.Weeks -> R.plurals.age_unit_weeks
    is PrettyAge.Months -> R.plurals.age_unit_months
    is PrettyAge.Years -> R.plurals.age_unit_years
  }

private val PrettyAge.lessSignificantUnit
  get() = when (this) {
    is PrettyAge.Days -> throw IllegalArgumentException("Days doesn't have a less significant value.")
    is PrettyAge.Weeks -> R.plurals.age_unit_days
    is PrettyAge.Months -> R.plurals.age_unit_weeks
    is PrettyAge.Years -> R.plurals.age_unit_months
  }
