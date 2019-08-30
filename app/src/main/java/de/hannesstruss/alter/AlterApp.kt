package de.hannesstruss.alter

import android.app.Application
import com.bugsnag.android.Bugsnag
import de.hannesstruss.alter.di.AppComponent
import de.hannesstruss.alter.di.DaggerAppComponent
import timber.log.Timber

class AlterApp : Application() {
  internal lateinit var appComponent: AppComponent

  override fun onCreate() {
    super.onCreate()

    Bugsnag.init(this, BuildConfig.BUGSNAG_API_KEY)

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

    appComponent = DaggerAppComponent.factory().create(this)
  }
}
