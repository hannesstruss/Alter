package de.hannesstruss.alter.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import java.time.LocalDate

object LocalDateAdapter : ColumnAdapter<LocalDate, String> {
  override fun decode(databaseValue: String): LocalDate {
    return LocalDate.parse(databaseValue)
  }

  override fun encode(value: LocalDate): String {
    return value.toString()
  }
}
