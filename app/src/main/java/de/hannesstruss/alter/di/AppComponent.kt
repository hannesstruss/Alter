package de.hannesstruss.alter.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import de.hannesstruss.alter.AlterApp
import de.hannesstruss.alter.domain.BabiesRepository
import javax.inject.Singleton

@Singleton
@Component(
  modules = [AppModule::class]
)
interface AppComponent {
  companion object {
    fun from(context: Context): AppComponent {
      return (context.applicationContext as AlterApp).appComponent
    }
  }

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance context: Context): AppComponent
  }

  fun babiesRepository(): BabiesRepository
}
