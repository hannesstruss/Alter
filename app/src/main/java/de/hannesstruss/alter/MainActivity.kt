package de.hannesstruss.alter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.hannesstruss.alter.di.ActivityComponent
import de.hannesstruss.alter.di.ActivityComponentHolder
import de.hannesstruss.alter.features.common.FeatureDependenciesServiceName

class MainActivity : AppCompatActivity() {

  private lateinit var activityComponent: ActivityComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent = ActivityComponentHolder.get(this)
    setContentView(R.layout.activity_main)
  }

  override fun getSystemService(name: String): Any {
    return when (name) {
      FeatureDependenciesServiceName -> activityComponent
      else -> super.getSystemService(name)
    }
  }
}
