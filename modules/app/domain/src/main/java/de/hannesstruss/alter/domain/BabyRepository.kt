package de.hannesstruss.alter.domain

import kotlinx.coroutines.flow.Flow

interface BabyRepository {
  fun all(): Flow<List<Baby>>
}
