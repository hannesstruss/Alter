package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.flowextensions.awaitFirst
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject
import javax.inject.Named

class BabyDetailViewModel
@Inject constructor(
  @Named("babyId") private val babyId: Long,
  private val babiesRepository: BabiesRepository
) : StateMachineViewModel<BabyDetailState, BabyDetailEvent>() {
  override val initialState = BabyDetailState.initial(babyId)

  override val stateMachine = createEngine {
    onInit {
      val baby = babiesRepository.byId(babyId).awaitFirst()
      enterState { copy(baby = baby) }
    }
  }
}
