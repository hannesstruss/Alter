package de.hannesstruss.alter.features.editbaby

import de.hannesstruss.alter.dates.Date

data class EditBabyState(
  val name: String = "",
  val parents: String = "",
  val birthDate: Date? = null
)
