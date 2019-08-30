package de.hannesstruss.alter.features.list

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.features.list.BabyListEvent.AddBaby
import de.hannesstruss.alter.features.list.BabyListEvent.ShowDetail
import de.hannesstruss.alter.navigation.Navigator
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject

class BabyListViewModel
@Inject internal constructor(
  private val babiesRepository: BabiesRepository,
  private val navigator: Navigator
) : StateMachineViewModel<BabyListState, BabyListEvent>() {
  override val initialState = BabyListState()
  override val stateMachine = createEngine {
    on<ShowDetail> {
      navigator.navigateTo(BabyListFragmentDirections.actionBabyListFragmentToBabyDetailFragment(it.babyId))
    }

    on<AddBaby> {
      navigator.navigateTo(BabyListFragmentDirections.actionBabyListFragmentToEditBabyFragment())
    }

    externalFlow {
      babiesRepository.getAll()
        .hookUp {
          enterState { copy(babies = it) }
        }
    }
  }
}
