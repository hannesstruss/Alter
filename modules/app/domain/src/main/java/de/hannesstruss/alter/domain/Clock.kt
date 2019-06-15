package de.hannesstruss.alter.domain

import java.time.OffsetDateTime

interface Clock {
  fun now(): OffsetDateTime
}
