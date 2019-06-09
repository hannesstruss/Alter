package de.hannesstruss.alter.features.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.alter.features.common.FeatureDependencyProvidingFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.FeatureComponent

class BabyListFragment :
  FeatureDependencyProvidingFragment<BabyListState, BabyListEvent, BabyListViewModel>() {
  override val layout = R.layout.baby_list_fragment
  override val viewModelClass = BabyListViewModel::class.java

  private lateinit var adapter: BabyListAdapter

  override fun events(): Flow<BabyListEvent> {
    return adapter.clickedIds.map { BabyListEvent.ShowDetail(it) }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    adapter = BabyListAdapter(LayoutInflater.from(view.context))
    val rv = view.findViewById<RecyclerView>(R.id.babies_list)
    rv.adapter = adapter

    val dividerDecoration = PaddedDividerItemDecoration(view.context)
    rv.addItemDecoration(dividerDecoration)
  }

  override fun render(state: BabyListState) {
    adapter.setBabies(state.babies)
  }

  override fun createFeatureComponent(): FeatureComponent<BabyListViewModel> {
    return DaggerBabyListComponent.builder().babyListDependencies(getFeatureDependencies()).build()
  }
}
