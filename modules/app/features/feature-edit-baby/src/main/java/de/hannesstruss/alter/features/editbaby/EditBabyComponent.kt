package de.hannesstruss.alter.features.editbaby

import dagger.Component
import de.hannesstruss.alter.domain.BabiesRepository
import shronq.statemachine.FeatureComponent

interface EditBabyDependencies {
  fun babiesRepository(): BabiesRepository
}

@Component(
  dependencies = [EditBabyDependencies::class]
)
interface EditBabyComponent : FeatureComponent<EditBabyViewModel> {
  @Component.Factory
  interface Factory {
    fun create(
      deps: EditBabyDependencies
    ): EditBabyComponent
  }
}
