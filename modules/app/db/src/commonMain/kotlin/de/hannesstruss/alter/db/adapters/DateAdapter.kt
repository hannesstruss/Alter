package de.hannesstruss.alter.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import de.hannesstruss.alter.dates.Date

object DateAdapter : ColumnAdapter<Date, String> {
  override fun decode(databaseValue: String): Date {
    val parts = databaseValue.split('-')
    println("Decoding parts: $parts")
    return Date(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
  }

  override fun encode(value: Date): String {
    println("Encoding date $value")
    val year = value.year.toString().padStart(4, '0')
    val month = value.month.toString().padStart(2, '0')
    val day = value.day.toString().padStart(2, '0')
    return "$year-$month-$day".also { println("Into: $it") }
  }
}
