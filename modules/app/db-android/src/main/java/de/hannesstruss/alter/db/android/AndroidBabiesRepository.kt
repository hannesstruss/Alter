package de.hannesstruss.alter.db.android

import com.squareup.sqldelight.runtime.coroutines.asFlow
import de.hannesstruss.alter.dates.Date
import de.hannesstruss.alter.db.AlterDatabase
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.domain.BabiesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AndroidBabiesRepository
@Inject constructor(
  private val alterDb: AlterDatabase
) : BabiesRepository {
  override suspend fun insert(
    name: String,
    parents: String,
    bornAt: Date?,
    dueOn: Date?
  ) = withContext(Dispatchers.IO) {
    alterDb.babyQueries.insert(name, parents, bornAt, dueOn)
  }

  override fun getAll(): Flow<List<Baby>> {
    return alterDb.babyQueries.selectAll().asFlow().map { it.executeAsList() }
  }

  override fun byId(id: Long): Flow<Baby> {
    return alterDb.babyQueries.byId(id).asFlow().map { it.executeAsOne() }
  }

  override suspend fun delete(baby: Baby) = withContext(Dispatchers.IO) {
    alterDb.babyQueries.deleteById(baby.id)
  }
}
