package de.hannesstruss.alter.db.ios

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import de.hannesstruss.alter.dates.Date
import de.hannesstruss.alter.db.AlterDatabase
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.db.adapters.DateAdapter

class IosBabyRepository {
  private val driver = NativeSqliteDriver(AlterDatabase.Schema, "alter3.sqlite")
  private val db = AlterDatabase(driver, Baby.Adapter(DateAdapter, DateAdapter))

  init {
    if (getBabies().executeAsList().isEmpty()) {
      db.babyQueries.initDummyData()
    }
  }

  fun getBabies(): Query<Baby> {
    return db.babyQueries.selectAll()
  }

  fun addBaby(
    name: String,
    parents: String,
    bornOn: Date?,
    dueOn: Date?
  ) {
    db.babyQueries.insert(name = name, parents = parents, born_at = bornOn, due_on = dueOn)
  }

  fun delete(id: Long) {
    db.babyQueries.deleteById(id)
  }
}
