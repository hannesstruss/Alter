package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.db.Baby

sealed class BabyDetailState {
  object Loading : BabyDetailState()
  data class Loaded(val baby: Baby) : BabyDetailState()

  companion object {
    fun initial(): BabyDetailState = Loading
  }
}
