package de.hannesstruss.alter.features.editbaby

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.AddBaby
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.ChangeDateOfBirth
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.ChangeName
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.ChangeParents
import de.hannesstruss.alter.features.editbaby.EditBabyEvent.PickDateOfBirth
import de.hannesstruss.alter.navigation.Navigator
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject

class EditBabyViewModel
@Inject constructor(
  private val navigator: Navigator,
  private val babiesRepository: BabiesRepository,
  private val effects: EditBabyViewEffects
) : StateMachineViewModel<EditBabyState, EditBabyEvent>() {
  override val initialState = EditBabyState()

  override val stateMachine = createEngine {
    on<ChangeName> {
      enterState { copy(name = it.name) }
    }

    on<ChangeParents> {
      enterState { copy(parents = it.parents) }
    }

    on<PickDateOfBirth> {
      effects.showDatePicker()
    }

    on<ChangeDateOfBirth> {
      enterState { copy(birthDate = it.dateOfBirth) }
    }

    on<AddBaby> {
      val state = getLatestState()

      babiesRepository.insert(
        name = state.name,
        parents = state.parents,
        bornAt = state.birthDate,
        dueOn = null
      )

      navigator.back()
    }
  }
}

