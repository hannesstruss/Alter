package de.hannesstruss.alter.dates

sealed class Age {
  companion object {
    fun of(dob: Date, today: Date): Age {
      check(today >= dob) { "dob must not be in the future: $dob > $today" }

      val fullYearsBetween = fullYearsBetween(dob, today)
      val fullMonthsBetween = fullMonthsBetween(dob, today)

      if (fullYearsBetween > 0) {
        return Years(fullYearsBetween, fullMonthsBetween)
      } else if (fullMonthsBetween >= 3) {
        return Months(fullMonthsBetween)
      } else {
        if (dob.month == today.month && dob.day <= today.day) {
          return daysOrWeeks(today.day - dob.day)
        }

        val dobDaysTilEndOfMonth = daysInMonth(dob.year, dob.month) - dob.day
        var daysBetween = dobDaysTilEndOfMonth + today.day
        var monthCursor = today.month
        var yearCursor = today.year

        while (!(monthCursor == dob.month + 1 && yearCursor == dob.year)) {
          monthCursor -= 1
          if (monthCursor == 0) {
            monthCursor = 12
            yearCursor -= 1
          }
          daysBetween += daysInMonth(yearCursor, monthCursor)
        }
        return daysOrWeeks(daysBetween)
      }
    }

    internal fun fullMonthsBetween(dob: Date, today: Date): Int {
      val partialMonthSubtraction = if (dob.day > today.day) 1 else 0
      val months = today.month - dob.month - partialMonthSubtraction
      return months + if (months < 0) 12 else 0
    }

    internal fun fullYearsBetween(dob: Date, today: Date): Int {
      val partialYearSubtraction = if (
        dob.month > today.month ||
        (dob.month == today.month && dob.day > today.day)
      ) 1 else 0
      return today.year - dob.year - partialYearSubtraction
    }

    private fun daysOrWeeks(days: Int): Age {
      return if (days < 14) {
        Days(days)
      } else {
        Weeks(days / 7)
      }
    }
  }

  data class Days(val days: Int) : Age()
  data class Weeks(val weeks: Int) : Age()
  data class Months(val months: Int) : Age()
  data class Years(val years: Int, val months: Int) : Age()
}

internal expect fun daysInMonth(year: Int, month: Int): Int
