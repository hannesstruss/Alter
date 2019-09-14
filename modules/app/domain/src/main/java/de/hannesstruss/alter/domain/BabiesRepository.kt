package de.hannesstruss.alter.domain

import de.hannesstruss.alter.db.Baby
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface BabiesRepository {
  fun getAll(): Flow<List<Baby>>
  fun byId(id: Long): Flow<Baby>
  suspend fun delete(baby: Baby)
  suspend fun insert(
    name: String,
    parents: String,
    bornAt: LocalDate?,
    dueOn: LocalDate?
  )
}
