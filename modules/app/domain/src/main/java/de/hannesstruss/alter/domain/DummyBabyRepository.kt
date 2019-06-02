package de.hannesstruss.alter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DummyBabyRepository : BabyRepository {
  override fun all(): Flow<List<Baby>> {
    return flow {
      val babies = listOf(
        Baby(
          name = "Lars Struß",
          parents = "Tatiana & Hannes",
          bornAt = OffsetDateTime.of(
            LocalDate.of(2019, 5, 8),
            LocalTime.of(23, 7),
            ZoneOffset.ofHours(2)
          ),
          dueOn = LocalDate.of(2019, 5, 1)
        )
      )

      emit(babies)
    }
  }
}
