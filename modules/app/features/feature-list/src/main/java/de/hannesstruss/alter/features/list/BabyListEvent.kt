package de.hannesstruss.alter.features.list

sealed class BabyListEvent {
  data class ShowDetail(val babyId: Long) : BabyListEvent()
}
