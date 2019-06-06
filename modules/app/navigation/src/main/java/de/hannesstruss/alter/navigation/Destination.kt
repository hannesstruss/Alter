package de.hannesstruss.alter.navigation

sealed class Destination {
  object BabyList : Destination()
  data class BabyDetail(val babyId: Long) : Destination()
}
