package de.hannesstruss.alter.domain

import de.hannesstruss.alter.db.Baby
import java.time.OffsetDateTime
import java.time.Period
import java.time.temporal.ChronoUnit

fun Baby.ageDays(now: OffsetDateTime): Int? {
  return born_at?.let { born_at ->
    Period.between(born_at.toLocalDate(), now.toLocalDate()).days
  }
}

sealed class PrettyAge {
  companion object {
    fun of(dob: OffsetDateTime, now: OffsetDateTime): PrettyAge {
      if (dob > now) {
        throw IllegalArgumentException("dob > now")
      }

      val oneMonthBefore = now.minusMonths(1)
      if (dob > oneMonthBefore) {
        return Days(ChronoUnit.DAYS.between(dob, now))
      }

      if (dob > now.minusYears(1)) {
        var months = (now.monthValue - dob.monthValue + 12).toLong() % 12
        if (months > 0 && dob.dayOfMonth > now.dayOfMonth) {
          months -= 1
        }
        val monthsBefore = now.minusMonths(months)
        val daysBetweenMonthsBefore = ChronoUnit.DAYS.between(dob, monthsBefore)

        return Months(months, daysBetweenMonthsBefore / 7)
      }

      var years = now.year - dob.year
      if (dob.withYear(now.year) > now) {
        years -= 1
      }
      return Years(years, ChronoUnit.MONTHS.between(dob, now) % 12)
    }

    fun weeksOf(dob: OffsetDateTime, now: OffsetDateTime): Weeks {
      if (dob > now) {
        throw IllegalArgumentException("dob > now")
      }
      val days = ChronoUnit.DAYS.between(dob, now)
      return Weeks(days.toInt() / 7L, days.toInt() % 7L)
    }
  }

  data class Days(val days: Long) : PrettyAge()
  data class Weeks(val weeks: Long, val days: Long) : PrettyAge()
  data class Months(val months: Long, val weeks: Long) : PrettyAge()
  data class Years(val years: Int, val months: Long) : PrettyAge()
}
