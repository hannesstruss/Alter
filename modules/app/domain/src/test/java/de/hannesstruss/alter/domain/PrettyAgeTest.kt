package de.hannesstruss.alter.domain

import com.google.common.truth.Truth.assertThat
import de.hannesstruss.alter.domain.PrettyAge.Days
import de.hannesstruss.alter.domain.PrettyAge.Months
import de.hannesstruss.alter.domain.PrettyAge.Weeks
import de.hannesstruss.alter.domain.PrettyAge.Years
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class PrettyAgeTest {
  /*
   <1 month: z days -or- y weeks z days
   <1 year: x months y weeks -or- y weeks z days
   >1 year: 1 year x months -or- y weeks z days
   */
  private val now = d(2019, 5, 1)

  @Test fun `calculates days correctly`() {
    assertResult(2019, 4, 20, Days(11))
  }

  @Test fun `calculates months correctly`() {
    assertResult(2019, 4, 1, Months(1, 0))
    assertResult(2019, 3, 25, Months(1, 1))
    assertResult(2019, 3, 2, Months(1, 4))
    assertResult(2019, 3, 1, Months(2, 0))
    assertResult(2018, 10, 1, Months(7, 0))
    assertResult(2018, 10, 15, Months(6, 2))
  }

  @Test fun `calculates years correctly`() {
    assertResult(2018, 5, 1, Years(1, 0))
    assertResult(2018, 3, 15, Years(1, 1))
    assertResult(2017, 3, 15, Years(2, 1))
    assertResult(2017, 5, 2, Years(1, 11))
  }

  @Test fun `calculates weeks correctly`() {
    assertThat(PrettyAge.weeksOf(d(2019, 4, 17), now)).isEqualTo(Weeks(2, 0))
    assertThat(PrettyAge.weeksOf(d(2019, 4, 16), now)).isEqualTo(Weeks(2, 1))
    assertThat(PrettyAge.weeksOf(d(2019, 3, 16), now)).isEqualTo(Weeks(6, 4))
  }

  private fun d(year: Int, month: Int, day: Int) =
    OffsetDateTime.of(LocalDate.of(year, month, day), LocalTime.MIDNIGHT, ZoneOffset.UTC)

  private fun assertResult(year: Int, month: Int, day: Int, result: PrettyAge) {
    assertThat(PrettyAge.of(d(year, month, day), now)).isEqualTo(result)
  }
}
