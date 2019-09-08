package de.hannesstruss.alter.features.babydetail

import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import de.hannesstruss.alter.domain.LessSignificantValueMode
import de.hannesstruss.alter.domain.format
import de.hannesstruss.alter.features.babydetail.BabyDetailEvent.CycleThroughAgeFormats
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import flowbinding.android.clicks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class BabyDetailFragment :
  FeatureDependencyProvidingFragment<BabyDetailState, BabyDetailEvent, BabyDetailViewModel>() {
  private val args: BabyDetailFragmentArgs by navArgs()
  private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

  override val layout = R.layout.baby_detail_fragment
  override val viewModelClass = BabyDetailViewModel::class.java

  override fun events(): Flow<BabyDetailEvent> {
    return requireView().findViewById<View>(R.id.txt_age).clicks()
      .map { CycleThroughAgeFormats }
  }

  override fun render(state: BabyDetailState) {
    if (state.baby != null) {
      with(requireView()) {
        val dob = state.baby.born_at?.let {
          dateFormatter.format(it)
        } ?: ""
        findViewById<TextView>(R.id.txt_dob).text = dob

        val due = state.baby.due_on?.let {
          dateFormatter.format(it)
        } ?: ""
        findViewById<TextView>(R.id.txt_due_date).text = due

        findViewById<TextView>(R.id.txt_name).text = state.baby.name
        findViewById<TextView>(R.id.txt_parents).text = state.baby.parents
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
