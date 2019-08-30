package de.hannesstruss.alter.features.editbaby

import java.time.LocalDate

sealed class EditBabyEvent {
  data class ChangeName(val name: String) : EditBabyEvent()
  data class ChangeParents(val parents: String) : EditBabyEvent()
  object PickDateOfBirth : EditBabyEvent()
  data class ChangeDateOfBirth(val dateOfBirth: LocalDate) : EditBabyEvent()
  object AddBaby : EditBabyEvent()
}
