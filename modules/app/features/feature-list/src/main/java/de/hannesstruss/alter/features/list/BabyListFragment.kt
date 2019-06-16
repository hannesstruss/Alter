package de.hannesstruss.alter.features.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import de.hannesstruss.alter.features.list.BabyListEvent.AddBaby
import de.hannesstruss.alter.features.list.BabyListEvent.ShowDetail
import de.hannesstruss.alter.flowextensions.mergeFlows
import flowbinding.material.itemClicks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent

class BabyListFragment :
  FeatureDependencyProvidingFragment<BabyListState, BabyListEvent, BabyListViewModel>() {
  override val layout = R.layout.baby_list_fragment
  override val viewModelClass = BabyListViewModel::class.java

  private val toolbar: Toolbar
    get() = requireView().findViewById(R.id.toolbar)

  private lateinit var adapter: BabyListAdapter

  override fun events(): Flow<BabyListEvent> {
    return mergeFlows(adapter.clickedIds.map { ShowDetail(it) },
      toolbar.itemClicks().map {
        when (it.itemId) {
          R.id.add_baby -> AddBaby
          else -> throw IllegalArgumentException("Unhandled item ID: ${it}")
        }
      }
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    adapter = BabyListAdapter(LayoutInflater.from(view.context))
    val rv = view.findViewById<RecyclerView>(R.id.babies_list)
    rv.adapter = adapter

    val dividerDecoration = PaddedDividerItemDecoration(view.context)
    rv.addItemDecoration(dividerDecoration)

    toolbar.inflateMenu(R.menu.toolbar)
  }

  override fun render(state: BabyListState) {
    adapter.setBabies(state.babies)
  }

  override fun createFeatureComponent(): FeatureComponent<BabyListViewModel> {
    return DaggerBabyListComponent.builder().babyListDependencies(getFeatureDependencies()).build()
  }
}
