package de.hannesstruss.alter.features.editbaby

import de.hannesstruss.alter.dates.Date

sealed class EditBabyEvent {
  data class ChangeName(val name: String) : EditBabyEvent()
  data class ChangeParents(val parents: String) : EditBabyEvent()
  object PickDateOfBirth : EditBabyEvent()
  data class ChangeDateOfBirth(val dateOfBirth: Date) : EditBabyEvent()
  object AddBaby : EditBabyEvent()
}
