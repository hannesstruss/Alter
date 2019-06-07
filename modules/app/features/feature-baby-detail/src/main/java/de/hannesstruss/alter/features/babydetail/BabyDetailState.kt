package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.db.Baby

data class BabyDetailState(
  val babyId: Long,
  val baby: Baby?
) {
  companion object {
    fun initial(babyId: Long): BabyDetailState = BabyDetailState(babyId = babyId, baby = null)
  }
}
