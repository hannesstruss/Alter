package de.hannesstruss.alter

import android.app.Application
import de.hannesstruss.alter.db.android.runDbTest
import timber.log.Timber

class AlterApp : Application() {
  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      val prefix = "ALTR"
      val tree = object : Timber.DebugTree() {
        override fun log(level: Int, tag: String?, message: String, t: Throwable?) {
          val p = "$prefix (${Thread.currentThread().name}) "
          super.log(level, tag, p + message, t)
        }
      }
      Timber.plant(tree)
    }

    runDbTest(this)
  }
}
