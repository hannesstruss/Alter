package de.hannesstruss.alter.features.editbaby

import java.time.LocalDate

data class EditBabyState(
  val name: String = "",
  val parents: String = "",
  val birthDate: LocalDate? = null
)
