package de.hannesstruss.alter.features.babydetail

import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shronq.statemachine.FeatureComponent
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject

sealed class BabyDetailState {
  object Loading : BabyDetailState()
  data class Loaded(val baby: Baby) : BabyDetailState()

  companion object {
    fun initial(): BabyDetailState = Loading
  }
}

sealed class BabyDetailEvent

class BabyDetailViewModel
@Inject constructor(): StateMachineViewModel<BabyDetailState, BabyDetailEvent>() {
  override val initialState = BabyDetailState.initial()
  override val stateMachine = createEngine {

  }
}

class BabyDetailFragment : FeatureDependencyProvidingFragment<BabyDetailState, BabyDetailEvent, BabyDetailViewModel>() {
  override val layout = R.layout.baby_detail_fragment
  override val viewModelClass = BabyDetailViewModel::class.java

  override fun events(): Flow<BabyDetailEvent> {
    return flow {}
  }

  override fun render(state: BabyDetailState) {
    // TODO
  }

  override fun createFeatureComponent(): FeatureComponent<BabyDetailViewModel> {
    return DaggerBabyDetailComponent.create()
  }
}
