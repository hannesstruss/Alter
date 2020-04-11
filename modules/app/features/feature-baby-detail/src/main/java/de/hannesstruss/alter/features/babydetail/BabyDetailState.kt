package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.dates.Age
import de.hannesstruss.alter.dates.Date
import de.hannesstruss.alter.db.Baby

data class BabyDetailState(
  val babyId: Long,
  val baby: Baby? = null,
  val today: Date,
  val showAgeAsWeeks: Boolean = false
) {
  val age: Age? = baby?.born_at?.let {
    if (showAgeAsWeeks) {
      TODO("Can't show weeks yet")
    } else {
      Age.of(it, today)
    }
  }
}
