package de.hannesstruss.alter.dates

import de.hannesstruss.alter.dates.Age.Days
import de.hannesstruss.alter.dates.Age.Months
import de.hannesstruss.alter.dates.Age.Weeks
import de.hannesstruss.alter.dates.Age.Years
import kotlin.test.Test
import kotlin.test.assertEquals

class AgeTest {
  /*
   <1 month: x days
   <3 months: x weeks
   <1 year: x months
   >1 year: 1 year x months
   */

  @Test fun `fullMonthsBetween works`() {
    fun test(dob: Date, today: Date, expected: Int) {
      assertEquals(
        expected,
        Age.fullMonthsBetween(dob, today)
      )
    }

    test(
      Date(2019, 2, 1),
      Date(2019, 2, 1),
      0
    )

    test(
      Date(2019, 1, 15),
      Date(2019, 2, 1),
      0
    )

    test(
      Date(2019, 1, 1),
      Date(2019, 2, 1),
      1
    )

    test(
      Date(2018, 10, 1),
      Date(2019, 2, 1),
      4
    )

    test(
      Date(2018, 10, 2),
      Date(2019, 2, 1),
      3
    )

    test(
      Date(2018, 2, 2),
      Date(2019, 2, 2),
      0
    )

    test(
      Date(2018, 2, 3),
      Date(2019, 2, 2),
      11
    )
  }

  @Test fun `fullYearsBetween works`() {
    fun test(dob: Date, today: Date, expected: Int) {
      assertEquals(
        expected,
        Age.fullYearsBetween(dob, today)
      )
    }

    test(
      Date(2019, 2, 2),
      Date(2019, 2, 2),
      0
    )

    test(
      Date(2018, 5, 3),
      Date(2019, 2, 2),
      0
    )

    test(
      Date(2018, 2, 3),
      Date(2019, 2, 2),
      0
    )

    test(
      Date(2018, 2, 2),
      Date(2019, 2, 2),
      1
    )

    test(
      Date(2017, 2, 2),
      Date(2019, 2, 2),
      2
    )

    test(
      Date(2018, 3, 15),
      Date(2019, 5, 1),
      1
    )
  }

  @Test fun `calculates same date correctly`() {
    assertResult(2019, 5, 1, Days(0))
  }

  @Test fun `calculates days correctly`() {
    assertResult(2019, 5, 1, Days(8), today = Date(2019, 5, 9))
    assertResult(2019, 4, 20, Days(11))
    assertResult(2019, 4, 24, Days(7))
  }

  @Test fun `calculates weeks correctly`() {
    assertResult(2019, 4, 1, Weeks(4))
    assertResult(2019, 3, 25, Weeks(5))
    assertResult(2019, 2, 28, Weeks(8))
    assertResult(2019, 2, 27, Weeks(9))
    assertResult(2019, 2, 6, Weeks(12))
    assertResult(2019, 2, 2, Weeks(12))
    assertResult(2019, 2, 1, Months(3))
  }

  @Test fun `calculates months correctly`() {
    assertResult(2019, 1, 1, Months(4))
    assertResult(2018, 10, 1, Months(7))
    assertResult(2018, 10, 15, Months(6))
  }

  @Test fun `calculates years correctly`() {
    assertResult(2018, 5, 1, Years(1, 0))
    assertResult(2018, 3, 15, Years(1, 1))
    assertResult(2017, 3, 15, Years(2, 1))
    assertResult(2017, 5, 2, Years(1, 11))
  }

  private fun d(year: Int, month: Int, day: Int) = Date(year, month, day)

  private fun assertResult(
    year: Int,
    month: Int,
    day: Int,
    result: Age,
    today: Date = d(2019, 5, 1)
  ) {
    assertEquals(result, Age.of(Date(year, month, day), today))
  }
}
