package shronq.statemachine.next

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class StateMachine2<StateT, EventT : Any, TransitionT>(
  initialState: StateT,
  events: Flow<EventT>,
  applyTransition: (StateT, TransitionT) -> StateT,
  block: StateMachineBuilder<StateT, EventT, TransitionT>.() -> Unit
) {
  val states = flow {
    coroutineScope {
      var state = initialState

      val builder = StateMachineBuilder<StateT, EventT, TransitionT>()
      block(builder)

      val transitions = Channel<TransitionT>(UNLIMITED) // TODO: unlimited?

      launch {
        events
          .collect { event ->
            val bindings = builder.eventBindings[event.javaClass]
            if (bindings != null) {
              for (binding in bindings) {
                @Suppress("UNCHECKED_CAST")
                val castedBinding = binding as ListenerBinding<StateT, EventT, TransitionT>

                val eventContext = object : EventContext<StateT, TransitionT> {
                  override suspend fun enterState(block: StateT.() -> TransitionT) {
                    val transition = state.block()
                    transitions.send(transition)
                  }

                  override suspend fun getLatestState(): StateT {
                    return state
                  }
                }
                castedBinding.listener(eventContext, event)
              }
            }
          }

      }

      emit(state)
      transitions.consumeEach { transition ->
        val nextState = applyTransition(state, transition)
        state = nextState
        println("Emitting $nextState")
        emit(nextState)
      }
    }
  }
}

class StateMachineBuilder<StateT, EventT, TransitionT> {
  @PublishedApi
  internal val eventBindings =
    mutableMapOf<Class<out EventT>, MutableList<ListenerBinding<StateT, out EventT, TransitionT>>>()

  inline fun <reified T : EventT> on(noinline block: suspend EventContext<StateT, TransitionT>.(T) -> Unit) {
    val bindings = eventBindings.getOrPut(T::class.java) { mutableListOf() }
    bindings.add(ListenerBinding(block))
  }
}

class ListenerBinding<StateT, SpecificIntentT, TransitionT>(
  val listener: suspend EventContext<StateT, TransitionT>.(SpecificIntentT) -> Unit,
  val distinct: Boolean = false
)

interface EventContext<StateT, TransitionT> {
  suspend fun enterState(block: StateT.() -> TransitionT)
  suspend fun getLatestState(): StateT
}
