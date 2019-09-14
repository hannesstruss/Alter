package de.hannesstruss.alter.features.list

import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import de.hannesstruss.alter.features.list.BabyListEvent.AddBaby
import de.hannesstruss.alter.features.list.BabyListEvent.ShowDetail
import de.hannesstruss.alter.features.list.databinding.BabyListFragmentBinding
import de.hannesstruss.alter.flowextensions.mergeFlows
import flowbinding.material.itemClicks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent

class BabyListFragment :
  FeatureDependencyProvidingFragment<BabyListState, BabyListEvent, BabyListViewModel, BabyListFragmentBinding>() {
  override val bindingInflater = infer(BabyListFragmentBinding::inflate)
  override val viewModelClass = BabyListViewModel::class.java

  private lateinit var adapter: BabyListAdapter

  override fun BabyListFragmentBinding.events(): Flow<BabyListEvent> {
    return mergeFlows(
      adapter.clickedIds.map { ShowDetail(it) },
      toolbar.itemClicks().map {
        when (it.itemId) {
          R.id.add_baby -> AddBaby
          else -> throw IllegalArgumentException("Unhandled item ID: ${it}")
        }
      }
    )
  }

  override fun BabyListFragmentBinding.onViewBound() {
    adapter = BabyListAdapter(layoutInflater)
    babiesList.adapter = adapter

    val dividerDecoration = PaddedDividerItemDecoration(requireContext())
    babiesList.addItemDecoration(dividerDecoration)

    toolbar.inflateMenu(R.menu.toolbar)
  }

  override fun BabyListFragmentBinding.render(state: BabyListState) {
    adapter.setBabies(state.babies)
  }

  override fun createFeatureComponent(): FeatureComponent<BabyListViewModel> {
    return DaggerBabyListComponent.builder().babyListDependencies(getFeatureDependencies()).build()
  }
}
