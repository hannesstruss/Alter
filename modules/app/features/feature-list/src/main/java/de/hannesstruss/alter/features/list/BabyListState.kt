package de.hannesstruss.alter.features.list

import de.hannesstruss.alter.db.Baby

data class BabyListState(
  val babies: List<Baby> = emptyList()
)
