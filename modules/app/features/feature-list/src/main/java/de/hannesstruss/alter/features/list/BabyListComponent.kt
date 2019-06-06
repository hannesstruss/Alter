package de.hannesstruss.alter.features.list

import dagger.Component
import de.hannesstruss.alter.domain.BabiesRepository
import shronq.statemachine.FeatureComponent

interface BabyListDependencies {
  fun babiesRepository(): BabiesRepository
}

@Component(
  dependencies = [BabyListDependencies::class]
)
interface BabyListComponent: FeatureComponent<BabyListViewModel>
