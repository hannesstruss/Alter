package shronq.statemachine

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.flow

class StateMachineTestAdapter<StateT, EventT : Any, TransitionT>(
  initialState: StateT,
  applyTransition: (StateT, TransitionT) -> StateT,
  block: StateMachineBuilder<StateT, EventT, TransitionT>.() -> Unit
) {
  private val events = BroadcastChannel<EventT>(500)
  private val stateMachine = StateMachine<StateT, EventT, TransitionT>(
    initialState = initialState,
    events = flow { events.consumeEach { emit(it) } },
    applyTransition = applyTransition,
    block = block
  )

  suspend fun send(event: EventT) {
    events.send(event)
  }

  val states get() = stateMachine.states
}
