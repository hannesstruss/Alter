package de.hannesstruss.alter.di

import dagger.Binds
import dagger.Module
import de.hannesstruss.alter.navigation.AACNavigator
import de.hannesstruss.alter.navigation.Navigator

@Module
abstract class ActivityModule {
  @Binds abstract fun navigator(aacNavigator: AACNavigator): Navigator
}
