package de.hannesstruss.alter.dates

import java.time.LocalDate

actual fun today(): Date {
  val localDate = LocalDate.now()
  return Date(localDate.year, localDate.monthValue, localDate.dayOfMonth)
}

actual typealias PlatformDate = LocalDate

actual fun Date.toPlatformDate(): PlatformDate {
  return LocalDate.of(year, month, day)
}

actual fun PlatformDate.toAlterDate(): Date {
  return Date(
    year = year,
    month = monthValue,
    day = dayOfMonth
  )
}
