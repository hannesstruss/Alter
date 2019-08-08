package de.hannesstruss.alter.features.editbaby

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.PickDateOfBirth
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject

class EditBabyViewModel
@Inject constructor(
  private val babiesRepository: BabiesRepository
) : StateMachineViewModel<EditBabyState, EditBabyEvent>() {
  override val initialState = EditBabyState()

  override val stateMachine = createEngine {
    on<PickDateOfBirth> {
      enterState { copy(showDateOfBirthPicker = true) }
    }
  }
}
