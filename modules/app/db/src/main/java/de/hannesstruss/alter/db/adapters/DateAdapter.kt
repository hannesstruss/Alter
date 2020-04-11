package de.hannesstruss.alter.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import de.hannesstruss.alter.dates.Date
import java.time.LocalDate

object DateAdapter : ColumnAdapter<Date, String> {
  override fun decode(databaseValue: String): Date {
    val parts = databaseValue.split('-')
    return Date(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
  }

  override fun encode(value: Date): String {
    return String.format("%04d-%02d-%02d", value.year, value.month, value.day)
  }
}
