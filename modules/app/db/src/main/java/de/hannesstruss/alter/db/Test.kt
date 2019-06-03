package de.hannesstruss.alter.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import de.hannesstruss.alter.db.adapters.LocalDateAdapter
import de.hannesstruss.alter.db.adapters.OffsetDateTimeAdapter
import timber.log.Timber

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
