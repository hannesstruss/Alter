package de.hannesstruss.alter

import android.widget.TextView
import flowbinding.clicks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shronq.statemachine.StateMachineFragment

class MainFragment : StateMachineFragment<HelloState, HelloEvent, MainViewModel>() {
  override val layout = R.layout.fragment_main
  override val viewModelClass = MainViewModel::class.java

  val txtHello get() = requireView().findViewById<TextView>(R.id.txt_hello)

  override fun events(): Flow<HelloEvent> {
    return txtHello.clicks().map { HelloEvent.Click }
  }

  override fun render(state: HelloState) {
    txtHello.text = state.counter.toString()
  }
}
