package de.hannesstruss.alter.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

sealed class PrettyAge {
  companion object {
    fun of(dob: LocalDate, now: LocalDate): PrettyAge {
      if (dob > now) {
        throw IllegalArgumentException("dob > now")
      }

      val oneMonthBefore = now.minusMonths(1)
      if (dob > oneMonthBefore) {
        return Days(ChronoUnit.DAYS.between(dob, now).toInt())
      }

      if (dob > now.minusYears(1)) {
        // Find out roughly how many months are between now and dob:
        var monthsBetween = (now.monthValue - dob.monthValue + 12) % 12

        if (monthsBetween > 0 && dob.dayOfMonth > now.dayOfMonth) {
          // If the day of the month of the dob is greater than that of now, that one month
          // is not a full month and we don't count it:
          monthsBetween -= 1
        } else if (monthsBetween == 0) {
          // If there are no months between, we must have hit an overflow
          // due to the modulo:
          monthsBetween = 12
        }
        val monthsBefore = now.minusMonths(monthsBetween.toLong())
        val daysBetweenMonthsBefore = ChronoUnit.DAYS.between(dob, monthsBefore).toInt()

        return Months(monthsBetween, daysBetweenMonthsBefore / 7)
      }

      var years = now.year - dob.year
      if (dob.withYear(now.year) > now) {
        years -= 1
      }
      return Years(years, ChronoUnit.MONTHS.between(dob, now).toInt() % 12)
    }

    fun weeksOf(dob: LocalDate, now: LocalDate): Weeks {
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
