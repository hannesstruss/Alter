package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.domain.Clock
import de.hannesstruss.alter.features.babydetail.BabyDetailEvent.CycleThroughAgeFormats
import de.hannesstruss.alter.features.babydetail.BabyDetailEvent.DeleteBaby
import de.hannesstruss.alter.flowextensions.awaitFirst
import de.hannesstruss.alter.navigation.Navigator
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject
import javax.inject.Named

class BabyDetailViewModel
@Inject constructor(
  @Named("babyId") private val babyId: Long,
  private val babiesRepository: BabiesRepository,
  clock: Clock,
  private val navigator: Navigator
) : StateMachineViewModel<BabyDetailState, BabyDetailEvent>() {
  override val initialState = BabyDetailState(babyId = babyId, today = clock.now().toLocalDate())

  override val stateMachine = createEngine {
    onInit {
      val baby = babiesRepository.byId(babyId).awaitFirst()
      enterState { copy(baby = baby) }
    }

    on<CycleThroughAgeFormats> {
      enterState { copy(showAgeAsWeeks = !showAgeAsWeeks) }
    }

    on<DeleteBaby> {
      babiesRepository.delete(getLatestState().baby!!)
      navigator.back()
    }
  }
}
