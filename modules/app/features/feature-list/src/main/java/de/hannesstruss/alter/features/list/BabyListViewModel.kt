package de.hannesstruss.alter.features.list

import de.hannesstruss.alter.domain.BabiesRepository
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject

class BabyListViewModel
@Inject internal constructor(
  private val babiesRepository: BabiesRepository
) : StateMachineViewModel<BabyListState, BabyListEvent>() {
  override val initialState = BabyListState()
  override val stateMachine = createEngine {
    externalFlow {
      babiesRepository.getAll()
        .hookUp {
          enterState { copy(babies = it) }
        }
    }
  }
}
