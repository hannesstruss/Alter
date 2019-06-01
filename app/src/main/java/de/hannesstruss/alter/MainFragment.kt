package de.hannesstruss.alter

import android.widget.TextView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import shronq.statemachine.StateMachineFragment

class MainFragment : StateMachineFragment<HelloState, HelloEvent, MainViewModel>() {
  override val layout = R.layout.fragment_main
  override val viewModelClass = MainViewModel::class.java

  val txtHello get() = requireView().findViewById<TextView>(R.id.txt_hello)

  override fun events(): Flow<HelloEvent> {
    val helloClicks = flowViaChannel<HelloEvent> { channel ->
      val txt = txtHello
      txt.setOnClickListener {
        channel.offer(HelloEvent.Click)
      }
      channel.invokeOnClose {
        txt.setOnClickListener(null)
      }
    }

    return helloClicks
  }

  override fun render(state: HelloState) {
    txtHello.text = state.counter.toString()
  }
}
