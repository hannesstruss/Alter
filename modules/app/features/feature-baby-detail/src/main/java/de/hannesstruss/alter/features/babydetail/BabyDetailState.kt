package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.domain.PrettyAge
import java.time.OffsetDateTime

data class BabyDetailState(
  val babyId: Long,
  val baby: Baby? = null,
  val now: OffsetDateTime,
  val showAgeAsWeeks: Boolean = false
) {
  val age: PrettyAge? = baby?.born_at?.let {
    if (showAgeAsWeeks) {
      PrettyAge.weeksOf(it, now)
    } else {
      PrettyAge.of(it, now)
    }
  }
}
