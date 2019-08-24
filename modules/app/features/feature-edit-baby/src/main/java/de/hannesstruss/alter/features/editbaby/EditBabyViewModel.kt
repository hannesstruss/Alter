package de.hannesstruss.alter.features.editbaby

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.ChangeDateOfBirth
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.PickDateOfBirth
import shronq.statemachine.StateMachineViewModel
import timber.log.Timber
import javax.inject.Inject

class EditBabyViewModel
@Inject constructor(
  private val babiesRepository: BabiesRepository,
  private val effects: EditBabyViewEffects
) : StateMachineViewModel<EditBabyState, EditBabyEvent>() {
  override val initialState = EditBabyState()

  override val stateMachine = createEngine {
    on<PickDateOfBirth> {
      effects.showDatePicker()
    }

    on<ChangeDateOfBirth> {
      Timber.d("Selected Date: ${it.dateOfBirth}")
    }
  }
}

