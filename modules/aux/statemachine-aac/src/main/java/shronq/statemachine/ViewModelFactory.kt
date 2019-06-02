package shronq.statemachine

import androidx.lifecycle.ViewModelProvider

interface ViewModelFactory : ViewModelProvider.Factory {
  companion object {
    val SERVICE_NAME = "SHRONQ_STATE_VIEW_MODEL_FACTORY"
  }
}

