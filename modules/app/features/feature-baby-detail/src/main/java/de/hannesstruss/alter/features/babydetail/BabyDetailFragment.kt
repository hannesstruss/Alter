package de.hannesstruss.alter.features.babydetail

import android.widget.TextView
import androidx.navigation.fragment.navArgs
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shronq.statemachine.FeatureComponent

class BabyDetailFragment :
  FeatureDependencyProvidingFragment<BabyDetailState, BabyDetailEvent, BabyDetailViewModel>() {
  private val args: BabyDetailFragmentArgs by navArgs()

  override val layout = R.layout.baby_detail_fragment
  override val viewModelClass = BabyDetailViewModel::class.java

  override fun events(): Flow<BabyDetailEvent> {
    return flow {}
  }

  override fun render(state: BabyDetailState) {
    if (state.baby != null) {
      with(requireView()) {
        findViewById<TextView>(R.id.txt_name).text = state.baby.name
        findViewById<TextView>(R.id.txt_parents).text = state.baby.parents
        findViewById<TextView>(R.id.txt_dob).text = state.baby.born_at?.toString() ?: "No DOB"
      }
    }
  }

  override fun createFeatureComponent(): FeatureComponent<BabyDetailViewModel> {
    return DaggerBabyDetailComponent.factory()
      .create(
        babyDetailDependencies = getFeatureDependencies(),
        babyId = args.babyId
      )
  }
}
