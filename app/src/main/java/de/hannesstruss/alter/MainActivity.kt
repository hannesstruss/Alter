package de.hannesstruss.alter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.hannesstruss.alter.di.FeatureDependencies
import de.hannesstruss.alter.features.common.FeatureDependenciesServiceName

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun getSystemService(name: String): Any {
    return when (name) {
      FeatureDependenciesServiceName -> FeatureDependencies.from(this)
      else -> super.getSystemService(name)
    }
  }
}
