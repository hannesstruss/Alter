package de.hannesstruss.alter.db.android

import com.squareup.sqldelight.runtime.coroutines.asFlow
import de.hannesstruss.alter.db.AlterDatabase
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.domain.BabiesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AndroidBabiesRepository
@Inject constructor(
  private val alterDb: AlterDatabase
) : BabiesRepository {
  override fun getAll(): Flow<List<Baby>> {
    return alterDb.babyQueries.selectAll().asFlow().map { it.executeAsList() }
  }

  override fun byId(id: Long): Flow<Baby> {
    return alterDb.babyQueries.byId(id).asFlow().map { it.executeAsOne() }
  }
}
