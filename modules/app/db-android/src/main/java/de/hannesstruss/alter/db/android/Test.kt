package de.hannesstruss.alter.db.android

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import de.hannesstruss.alter.db.AlterDatabase
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.db.adapters.LocalDateAdapter
import de.hannesstruss.alter.db.adapters.OffsetDateTimeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

fun runDbTest(context: Context) {
  val driver = AndroidSqliteDriver(AlterDatabase.Schema, context, "test.db")
  val adapter = Baby.Adapter(
    born_atAdapter = OffsetDateTimeAdapter,
    due_onAdapter = LocalDateAdapter
  )
  val db = AlterDatabase(driver, adapter)
  val babyQueries = db.babyQueries
  GlobalScope.launch(Dispatchers.IO) {

    Timber.d("Doing all the DB Stuff")
    babyQueries.clear()
    babyQueries.initDummyData()
    babyQueries.selectAll().asFlow().collect {
      val babies = it.executeAsList()
      Timber.d(babies.toString())
    }
  }

  GlobalScope.launch(Dispatchers.IO) {
    delay(2000)
    babyQueries.clear()
    delay(2000)
    babyQueries.initDummyData()
  }
}
