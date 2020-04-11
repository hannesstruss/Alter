package de.hannesstruss.alter.dates

data class Date(val year: Int, val month: Int, val day: Int) {
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

expect fun today(): Date
