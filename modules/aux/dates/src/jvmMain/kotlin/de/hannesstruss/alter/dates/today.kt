package de.hannesstruss.alter.dates

import java.time.LocalDate

actual fun today(): Date {
  val localDate = LocalDate.now()
  return Date(localDate.year, localDate.monthValue, localDate.dayOfMonth)
}
