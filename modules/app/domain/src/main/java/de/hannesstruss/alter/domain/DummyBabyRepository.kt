package de.hannesstruss.alter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DummyBabyRepository : BabyRepository {
  override fun all(): Flow<List<Baby>> {
    return flowViaChannel {
      val babies = (0..3).map {
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
      }

      it.offer(babies)
    }
  }
}
