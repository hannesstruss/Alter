package de.hannesstruss.alter.dates

data class Date(val year: Int, val month: Int, val day: Int) {
  init {
    check(year > 0 && month > 0 && month <= 31 && day > 0 && day <= 31) {
      "Invalid date components: $year-$month-$day"
    }
  }

  operator fun compareTo(other: Date): Int {
    return if (year != other.year) {
      year.compareTo(other.year)
    } else if (month != other.month) {
      month.compareTo(other.month)
    } else if (day != other.day) {
      day.compareTo(other.day)
    } else {
      0
    }
  }
}

expect class PlatformDate
expect fun today(): Date
expect fun Date.toPlatformDate(): PlatformDate
expect fun PlatformDate.toAlterDate(): Date
