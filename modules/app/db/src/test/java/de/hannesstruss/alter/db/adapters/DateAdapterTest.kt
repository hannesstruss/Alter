package de.hannesstruss.alter.db.adapters

import com.google.common.truth.Truth.assertThat
import de.hannesstruss.alter.dates.Date
import org.junit.Test

class DateAdapterTest {
  @Test fun decode() {
    val str = "2019-05-03"
    assertThat(DateAdapter.decode(str)).isEqualTo(Date(2019, 5, 3))
  }

  @Test fun encode() {
    val date = Date(2019, 5, 3)
    assertThat(DateAdapter.encode(date)).isEqualTo("2019-05-03")
  }

  @Test fun `encode year with leading zeros`() {
    val date = Date(400, 5, 3)
    assertThat(DateAdapter.encode(date)).isEqualTo("0400-05-03")
  }
}
