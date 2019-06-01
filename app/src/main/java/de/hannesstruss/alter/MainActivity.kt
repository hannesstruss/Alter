package de.hannesstruss.alter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
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
          return MainViewModel() as T
        }
      }
    }
    return super.getSystemService(name)
  }
}
