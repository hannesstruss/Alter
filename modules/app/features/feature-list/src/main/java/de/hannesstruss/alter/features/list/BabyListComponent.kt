package de.hannesstruss.alter.features.list

import dagger.Component
import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.navigation.Navigator
import shronq.statemachine.FeatureComponent

interface BabyListDependencies {
  fun babiesRepository(): BabiesRepository
  fun navigator(): Navigator
}

@Component(
  dependencies = [BabyListDependencies::class]
)
interface BabyListComponent: FeatureComponent<BabyListViewModel>
