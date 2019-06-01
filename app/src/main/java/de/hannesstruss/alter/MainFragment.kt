package de.hannesstruss.alter

import android.widget.TextView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shronq.statemachine.StateMachineFragment

class MainFragment : StateMachineFragment<HelloState, HelloEvent, MainViewModel>() {
  override val layout = R.layout.fragment_main
  override val viewModelClass = MainViewModel::class.java

  override fun events(): Flow<HelloEvent> {
    return flow {}
  }

  override fun render(state: HelloState) {
    requireView().findViewById<TextView>(R.id.txt_hello)
  }
}
