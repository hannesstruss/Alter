package de.hannesstruss.alter.domain

import de.hannesstruss.alter.db.Baby
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.OffsetDateTime

interface BabiesRepository {
  fun getAll(): Flow<List<Baby>>
  fun byId(id: Long): Flow<Baby>
  suspend fun insert(
    name: String,
    parents: String,
    bornAt: OffsetDateTime?,
    dueOn: LocalDate?
  )
}
