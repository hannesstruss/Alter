package de.hannesstruss.alter.dates

import java.time.YearMonth

internal actual fun daysInMonth(year: Int, month: Int): Int {
  return YearMonth.of(year, month).lengthOfMonth()
}
