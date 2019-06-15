package de.hannesstruss.alter.domain

import java.time.OffsetDateTime
import javax.inject.Inject

class WallClock
@Inject constructor() : Clock {
  override fun now() = OffsetDateTime.now()
}
