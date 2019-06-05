package de.hannesstruss.alter.db.android

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import de.hannesstruss.alter.db.AlterDatabase
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.db.adapters.LocalDateAdapter
import de.hannesstruss.alter.db.adapters.OffsetDateTimeAdapter

fun alterDb(context: Context): AlterDatabase {
  val driver = AndroidSqliteDriver(AlterDatabase.Schema, context, "test.db")
  val adapter = Baby.Adapter(
    born_atAdapter = OffsetDateTimeAdapter,
    due_onAdapter = LocalDateAdapter
  )
  return AlterDatabase(driver, adapter)
}
