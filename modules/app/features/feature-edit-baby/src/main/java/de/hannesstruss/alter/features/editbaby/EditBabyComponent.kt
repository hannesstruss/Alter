package de.hannesstruss.alter.features.editbaby

import dagger.BindsInstance
import dagger.Component
import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.navigation.Navigator
import shronq.statemachine.FeatureComponent

interface EditBabyDependencies {
  fun babiesRepository(): BabiesRepository
  fun navigator(): Navigator
}

@Component(
  dependencies = [EditBabyDependencies::class]
)
interface EditBabyComponent : FeatureComponent<EditBabyViewModel> {
  @Component.Factory
  interface Factory {
    fun create(
      deps: EditBabyDependencies,
      @BindsInstance effects: EditBabyViewEffects
    ): EditBabyComponent
  }
}
