package de.hannesstruss.alter.features.list

import android.widget.TextView
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.domain.BabiesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shronq.statemachine.StateMachineFragment
import shronq.statemachine.StateMachineViewModel

data class BabyListState(
  val babies: List<Baby> = emptyList()
)

sealed class BabyListEvent

fun viewModel(repo: BabiesRepository) = BabyListViewModel(repo)

class BabyListViewModel internal constructor(
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

class BabyListFragment : StateMachineFragment<BabyListState, BabyListEvent, BabyListViewModel>() {
  override val layout = R.layout.baby_list_fragment
  override val viewModelClass = BabyListViewModel::class.java

  override fun events(): Flow<BabyListEvent> = flow {}

  override fun render(state: BabyListState) {
    requireView().findViewById<TextView>(R.id.txt_babies).text = state.babies.map { it.name }.joinToString("\n")
  }
}
