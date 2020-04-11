package de.hannesstruss.alter.features.babydetail

import androidx.navigation.fragment.navArgs
import de.hannesstruss.alter.domain.format
import de.hannesstruss.alter.features.babydetail.BabyDetailEvent.CycleThroughAgeFormats
import de.hannesstruss.alter.features.babydetail.BabyDetailEvent.DeleteBaby
import de.hannesstruss.alter.features.babydetail.databinding.BabyDetailFragmentBinding
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import de.hannesstruss.alter.flowextensions.mergeFlows
import flowbinding.android.clicks
import flowbinding.material.itemClicks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent

class BabyDetailFragment :
  FeatureDependencyProvidingFragment<BabyDetailState, BabyDetailEvent, BabyDetailViewModel, BabyDetailFragmentBinding>() {
  private val args: BabyDetailFragmentArgs by navArgs()

  override val bindingInflater = infer(BabyDetailFragmentBinding::inflate)
  override val viewModelClass = BabyDetailViewModel::class.java

  override fun BabyDetailFragmentBinding.events(): Flow<BabyDetailEvent> {
    return mergeFlows(
      txtAge.clicks().map { CycleThroughAgeFormats },
      toolbar.itemClicks(R.id.delete).map { DeleteBaby }
    )
  }

  override fun BabyDetailFragmentBinding.onViewBound() {
    toolbar.inflateMenu(R.menu.baby_detail)
  }

  override fun BabyDetailFragmentBinding.render(state: BabyDetailState) {
    if (state.baby != null) {
      val dob = state.baby.born_at?.toString() ?: ""
      txtDob.text = dob

      val due = state.baby.due_on?.toString() ?: ""
      txtDueDate.text = due

      txtName.text = state.baby.name
      txtParents.text = state.baby.parents
      txtAge.text = state.age?.format(
        context = requireContext(),
        numberTextAppearance = R.style.TextAppearance_MaterialComponents_Headline5
      ) ?: ""
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
