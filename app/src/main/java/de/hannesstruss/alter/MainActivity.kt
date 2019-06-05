package de.hannesstruss.alter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import de.hannesstruss.alter.db.android.AndroidBabiesRepository
import de.hannesstruss.alter.db.android.alterDb
import de.hannesstruss.alter.features.list.viewModel
import shronq.statemachine.ViewModelFactory

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun getSystemService(name: String): Any {
    if (name == ViewModelFactory.SERVICE_NAME) {
      return object : ViewModelFactory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          val repo = AndroidBabiesRepository(alterDb(this@MainActivity))
          return viewModel(repo) as T
        }
      }
    }
    return super.getSystemService(name)
  }
}
