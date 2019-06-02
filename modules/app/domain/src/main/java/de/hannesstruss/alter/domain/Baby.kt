package de.hannesstruss.alter.domain

import java.time.LocalDate
import java.time.OffsetDateTime

data class Baby(
  val name: String,
  val parents: String,
  val bornAt: OffsetDateTime,
  val dueOn: LocalDate
)
