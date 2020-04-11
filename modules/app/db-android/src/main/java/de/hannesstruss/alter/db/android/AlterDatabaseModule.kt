package de.hannesstruss.alter.db.android

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import de.hannesstruss.alter.db.AlterDatabase
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.db.adapters.DateAdapter

@Module
class AlterDatabaseModule {
  @Provides fun alterDatabase(context: Context): AlterDatabase {
    val driver = AndroidSqliteDriver(AlterDatabase.Schema, context, "alter.db")
    val adapter = Baby.Adapter(
      born_atAdapter = DateAdapter,
      due_onAdapter = DateAdapter
    )
    return AlterDatabase(driver, adapter)
  }
}
