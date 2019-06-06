package de.hannesstruss.alter.features.list

import android.widget.TextView
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shronq.statemachine.FeatureComponent

class BabyListFragment : FeatureDependencyProvidingFragment<BabyListState, BabyListEvent, BabyListViewModel>() {
  override val layout = R.layout.baby_list_fragment
  override val viewModelClass = BabyListViewModel::class.java

  override fun events(): Flow<BabyListEvent> = flow {}

  override fun render(state: BabyListState) {
    requireView().findViewById<TextView>(R.id.txt_babies).text = state.babies.map { it.name }.joinToString("\n")
  }

  override fun createFeatureComponent(): FeatureComponent<BabyListViewModel> {
    return DaggerBabyListComponent.builder().babyListDependencies(getFeatureDependencies()).build()
  }
}
