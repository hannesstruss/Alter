package de.hannesstruss.alter.di

import dagger.Binds
import dagger.Module
import de.hannesstruss.alter.db.android.AlterDatabaseModule
import de.hannesstruss.alter.db.android.AndroidBabiesRepository
import de.hannesstruss.alter.domain.BabiesRepository
import de.hannesstruss.alter.domain.Clock
import de.hannesstruss.alter.domain.WallClock
import javax.inject.Singleton

@Module(
  includes = [AlterDatabaseModule::class]
)
abstract class AppModule {
  @Binds @Singleton abstract fun babiesRepository(impl: AndroidBabiesRepository): BabiesRepository
  @Binds abstract fun clock(wallClock: WallClock): Clock
}
