package de.hannesstruss.alter

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.hannesstruss.alter.R.id.txt_hello
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import shronq.statemachine.StateMachine

class MainActivity : AppCompatActivity() {

  data class HelloState(
    val counter: Int = 0
  )

  sealed class HelloEvent {
    object Click : HelloEvent()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val txt = findViewById<TextView>(txt_hello)

    val events = flowViaChannel<HelloEvent> { channel ->
      txt.setOnClickListener {
        GlobalScope.launch {
          channel.send(HelloEvent.Click)
        }
      }

      channel.invokeOnClose {
        txt.setOnClickListener(null)
      }
    }


    val machine = StateMachine<HelloState, HelloEvent, HelloState>(
      initialState = HelloState(),
      events = events,
      applyTransition = { _, nextState -> nextState }
    ) {
      onInit {
        while (true) {
          enterState { copy(counter = counter + 1) }
          delay(1000)
        }
      }

      on<HelloEvent.Click> {
        repeat(50) { n ->
          enterState { copy(counter = counter + 1) }
          delay(n.toLong() * 10)
        }
      }
    }

    GlobalScope.launch {
      machine.states.collect {
        withContext(Dispatchers.Main) {
          txt.text = it.counter.toString()
        }
      }
      println("Done")
    }
  }
}
