package de.hannesstruss.alter.db.android

import com.squareup.sqldelight.runtime.coroutines.asFlow
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
  override suspend fun insert(baby: Baby) = withContext(Dispatchers.IO) {
    alterDb.babyQueries.insert(baby.name, baby.parents, baby.born_at, baby.due_on)
  }

  override fun getAll(): Flow<List<Baby>> {
    return alterDb.babyQueries.selectAll().asFlow().map { it.executeAsList() }
  }

  override fun byId(id: Long): Flow<Baby> {
    return alterDb.babyQueries.byId(id).asFlow().map { it.executeAsOne() }
  }
}
