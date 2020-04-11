package de.hannesstruss.alter.db.adapters

import de.hannesstruss.alter.dates.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class DateAdapterTest {
  @Test fun decode() {
    val str = "2019-05-03"
    assertEquals(Date(2019, 5, 3), DateAdapter.decode(str))
  }

  @Test fun encode() {
    val date = Date(2019, 5, 3)
    assertEquals("2019-05-03", DateAdapter.encode(date))
  }

  @Test fun `encode year with leading zeros`() {
    val date = Date(400, 5, 3)
    assertEquals("0400-05-03", DateAdapter.encode(date))
  }
}
