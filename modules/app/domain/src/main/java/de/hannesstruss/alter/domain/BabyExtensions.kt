package de.hannesstruss.alter.domain

import de.hannesstruss.alter.db.Baby
import java.time.OffsetDateTime
import java.time.Period

fun Baby.ageDays(now: OffsetDateTime): Int? {
  return born_at?.let { born_at ->
    Period.between(born_at.toLocalDate(), now.toLocalDate()).days
  }
}
