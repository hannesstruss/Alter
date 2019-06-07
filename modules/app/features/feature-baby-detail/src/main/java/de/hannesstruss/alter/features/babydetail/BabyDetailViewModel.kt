package de.hannesstruss.alter.features.babydetail

import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject

class BabyDetailViewModel
@Inject constructor(): StateMachineViewModel<BabyDetailState, BabyDetailEvent>() {
  override val initialState =
    BabyDetailState.initial()
  override val stateMachine = createEngine {

  }
}
