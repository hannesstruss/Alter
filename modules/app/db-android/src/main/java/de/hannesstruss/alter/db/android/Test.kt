package de.hannesstruss.alter.db.android

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.db.Database
import de.hannesstruss.alter.db.adapters.LocalDateAdapter
import de.hannesstruss.alter.db.adapters.OffsetDateTimeAdapter
import timber.log.Timber

class Cool(val baby: Baby)

fun runDbTest(context: Context) {
  val driver = AndroidSqliteDriver(Database.Schema, context, "test.db")
  val adapter = Baby.Adapter(
    born_atAdapter = OffsetDateTimeAdapter,
    due_onAdapter = LocalDateAdapter
  )
  val db = Database(driver, adapter)
  val babyQueries = db.babyQueries
  babyQueries.clear()
  babyQueries.initDummyData()
  babyQueries.selectAll().executeAsList().forEach {
    Timber.d(it.toString())
  }
}
