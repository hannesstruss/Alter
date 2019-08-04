package de.hannesstruss.alter.features.editbaby

import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shronq.statemachine.FeatureComponent
import shronq.statemachine.StateMachineViewModel
import javax.inject.Inject

class EditBabyState
class EditBabyEvent

class EditBabyViewModel
@Inject constructor(
  private val babiesRepository: BabiesRepository
) : StateMachineViewModel<EditBabyState, EditBabyEvent>() {
  override val initialState = EditBabyState()

  override val stateMachine = createEngine {

  }
}

class EditBabyFragment : FeatureDependencyProvidingFragment<EditBabyState, EditBabyEvent, EditBabyViewModel>() {
  override val layout = R.layout.edit_baby_fragment
  override val viewModelClass = EditBabyViewModel::class.java

  override fun createFeatureComponent(): FeatureComponent<EditBabyViewModel> {
    TODO("not implemented")
  }

  override fun events(): Flow<EditBabyEvent> {
    return flow {}
  }

  override fun render(state: EditBabyState) {
    // TODO
  }
}
