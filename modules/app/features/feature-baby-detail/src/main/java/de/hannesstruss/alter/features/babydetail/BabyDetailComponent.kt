package de.hannesstruss.alter.features.babydetail

import dagger.BindsInstance
import dagger.Component
import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.domain.Clock
import shronq.statemachine.FeatureComponent
import javax.inject.Named

interface BabyDetailDependencies {
  fun babiesRepository(): BabiesRepository
  fun clock(): Clock
}

@Component(
  dependencies = [BabyDetailDependencies::class]
)
interface BabyDetailComponent : FeatureComponent<BabyDetailViewModel> {
  @Component.Factory
  interface Factory {
    fun create(
      babyDetailDependencies: BabyDetailDependencies,
      @BindsInstance @Named("babyId") babyId: Long
    ): BabyDetailComponent
  }
}
