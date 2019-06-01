package de.hannesstruss.alter

import de.hannesstruss.alter.HelloEvent.Click
import kotlinx.coroutines.delay
import shronq.statemachine.StateMachineViewModel

class MainViewModel : StateMachineViewModel<HelloState, HelloEvent>() {
  override val initialState = HelloState()
  override val stateMachine = createEngine {
    onInit {
      while (true) {
        enterState { copy(counter = counter + 1) }
        delay(1000)
      }
    }

    on<Click> {
      repeat(50) { n ->
        enterState { copy(counter = counter + 1) }
        delay(n.toLong() * 10)
      }
    }
  }
}
