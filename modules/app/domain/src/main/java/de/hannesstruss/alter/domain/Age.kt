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
        return Days(ChronoUnit.DAYS.between(dob, now).toInt())
      }

      if (dob > now.minusYears(1)) {
        var months = (now.monthValue - dob.monthValue + 12) % 12
        if (months > 0 && dob.dayOfMonth > now.dayOfMonth) {
          months -= 1
        }
        val monthsBefore = now.minusMonths(months.toLong())
        val daysBetweenMonthsBefore = ChronoUnit.DAYS.between(dob, monthsBefore).toInt()

        return Months(months, daysBetweenMonthsBefore / 7)
      }

      var years = now.year - dob.year
      if (dob.withYear(now.year) > now) {
        years -= 1
      }
      return Years(years, ChronoUnit.MONTHS.between(dob, now).toInt() % 12)
    }

    fun weeksOf(dob: OffsetDateTime, now: OffsetDateTime): Weeks {
      if (dob > now) {
        throw IllegalArgumentException("dob > now")
      }
      val days = ChronoUnit.DAYS.between(dob, now).toInt()
      return Weeks(days / 7, days % 7)
    }
  }

  abstract val significantValue: Int
  abstract val lessSignificantValue: Int

  data class Days(val days: Int) : PrettyAge() {
    override val significantValue = days
    override val lessSignificantValue = 0
  }
  data class Weeks(val weeks: Int, val days: Int) : PrettyAge() {
    override val significantValue = weeks
    override val lessSignificantValue = days
  }
  data class Months(val months: Int, val weeks: Int) : PrettyAge() {
    override val significantValue = months
    override val lessSignificantValue = weeks
  }
  data class Years(val years: Int, val months: Int) : PrettyAge() {
    override val significantValue = years
    override val lessSignificantValue = months
  }
}
