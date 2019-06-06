package de.hannesstruss.alter.di

import dagger.Binds
import dagger.Module
import de.hannesstruss.alter.db.android.AlterDatabaseModule
import de.hannesstruss.alter.db.android.AndroidBabiesRepository
import de.hannesstruss.alter.domain.BabiesRepository
import javax.inject.Singleton

@Module(
  includes = [AlterDatabaseModule::class]
)
abstract class AppModule {
  @Binds @Singleton abstract fun babiesRepository(impl: AndroidBabiesRepository): BabiesRepository
}
