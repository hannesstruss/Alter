package de.hannesstruss.alter.di

import dagger.BindsInstance
import dagger.Component

@PerActivity
@Component(
  dependencies = [AppComponent::class],
  modules = [ActivityModule::class]
)
interface ActivityComponent : FeatureDependencies {
  fun activityFinder(): ActivityFinder

  @Component.Factory
  interface Factory {
    fun create(
      @BindsInstance activityFinder: ActivityFinder,
      appComponent: AppComponent
    ): ActivityComponent
  }
}
