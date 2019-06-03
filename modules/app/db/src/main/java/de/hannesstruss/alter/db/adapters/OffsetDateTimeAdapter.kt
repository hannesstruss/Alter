package de.hannesstruss.alter.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import java.time.OffsetDateTime

object OffsetDateTimeAdapter : ColumnAdapter<OffsetDateTime, String> {
  override fun decode(databaseValue: String): OffsetDateTime {
    return OffsetDateTime.parse(databaseValue)
  }

  override fun encode(value: OffsetDateTime): String {
    return value.toString()
  }
}
