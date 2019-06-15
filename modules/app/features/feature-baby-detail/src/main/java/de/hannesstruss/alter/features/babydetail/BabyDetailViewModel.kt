package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.domain.Clock
import de.hannesstruss.alter.features.babydetail.BabyDetailEvent.CycleThroughAgeFormats
import de.hannesstruss.alter.flowextensions.awaitFirst
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject
import javax.inject.Named

class BabyDetailViewModel
@Inject constructor(
  @Named("babyId") private val babyId: Long,
  private val babiesRepository: BabiesRepository,
  clock: Clock
) : StateMachineViewModel<BabyDetailState, BabyDetailEvent>() {
  override val initialState = BabyDetailState(babyId = babyId, now = clock.now())

  override val stateMachine = createEngine {
    onInit {
      val baby = babiesRepository.byId(babyId).awaitFirst()
      enterState { copy(baby = baby) }
    }

    on<CycleThroughAgeFormats> {
      enterState { copy(showAgeAsWeeks = !showAgeAsWeeks) }
    }
  }
}
