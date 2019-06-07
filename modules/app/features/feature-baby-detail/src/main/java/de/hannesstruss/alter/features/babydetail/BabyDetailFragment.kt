package de.hannesstruss.alter.features.babydetail

import androidx.navigation.fragment.navArgs
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shronq.statemachine.FeatureComponent

class BabyDetailFragment : FeatureDependencyProvidingFragment<BabyDetailState, BabyDetailEvent, BabyDetailViewModel>() {
  private val args: BabyDetailFragmentArgs by navArgs()

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
