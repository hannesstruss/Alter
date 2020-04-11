package de.hannesstruss.alter.domain

import de.hannesstruss.alter.dates.Date
import de.hannesstruss.alter.db.Baby
import kotlinx.coroutines.flow.Flow

interface BabiesRepository {
  fun getAll(): Flow<List<Baby>>
  fun byId(id: Long): Flow<Baby>
  suspend fun delete(baby: Baby)
  suspend fun insert(
    name: String,
    parents: String,
    bornAt: Date?,
    dueOn: Date?
  )
}
