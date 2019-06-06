package de.hannesstruss.alter.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
  modules = [AppModule::class]
)
interface AppComponent : FeatureDependencies {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance context: Context): AppComponent
  }
}
