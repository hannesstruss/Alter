package de.hannesstruss.alter.features.babydetail

import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import de.hannesstruss.alter.domain.LessSignificantValueMode
import de.hannesstruss.alter.domain.format
import de.hannesstruss.alter.features.babydetail.BabyDetailEvent.CycleThroughAgeFormats
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import flowbinding.clicks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent

class BabyDetailFragment :
  FeatureDependencyProvidingFragment<BabyDetailState, BabyDetailEvent, BabyDetailViewModel>() {
  private val args: BabyDetailFragmentArgs by navArgs()

  override val layout = R.layout.baby_detail_fragment
  override val viewModelClass = BabyDetailViewModel::class.java

  override fun events(): Flow<BabyDetailEvent> {
    return requireView().findViewById<View>(R.id.txt_age).clicks()
      .map { CycleThroughAgeFormats }
  }

  override fun render(state: BabyDetailState) {
    if (state.baby != null) {
      with(requireView()) {
        findViewById<TextView>(R.id.txt_name).text = state.baby.name
        findViewById<TextView>(R.id.txt_parents).text = state.baby.parents
        findViewById<TextView>(R.id.txt_dob).text = state.baby.born_at?.toString() ?: "No DOB"
        findViewById<TextView>(R.id.txt_age).text = state.age?.format(
          context = context,
          lessSignificantValueMode = LessSignificantValueMode.IfNonZero,
          numberTextAppearance = R.style.TextAppearance_MaterialComponents_Headline5
        ) ?: ""
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
