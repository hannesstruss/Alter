package de.hannesstruss.alter

import android.app.Application
import de.hannesstruss.alter.db.runDbTest
import timber.log.Timber

class AlterApp : Application() {
  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    runDbTest(this)
  }
}
