package shronq.statemachine.next

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.SendChannel
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
      val getLatestState: () -> StateT = { state }

      val builder = StateMachineBuilder<StateT, EventT, TransitionT>()
      block(builder)

      val transitions = Channel<TransitionT>(UNLIMITED) // TODO: unlimited?

      hookUpEventBindings(getLatestState, events, transitions, builder.eventBindings)
      hookUpExternalFlows(getLatestState, transitions, builder.externalFlowBindings)

      emit(state)
      transitions.consumeEach { transition ->
        val nextState = applyTransition(state, transition)
        state = nextState
        println("Emitting $nextState")
        emit(nextState)
      }
    }
  }

  private fun CoroutineScope.hookUpEventBindings(
    getLatestState: () -> StateT,
    events: Flow<EventT>,
    transitions: SendChannel<TransitionT>,
    eventBindings: Map<Class<out EventT>, List<ListenerBinding<StateT, out EventT, TransitionT>>>
  ) {
    launch {
      events
        .collect { event ->
          val bindings = eventBindings[event.javaClass]
          if (bindings != null) {
            for (binding in bindings) {
              @Suppress("UNCHECKED_CAST")
              val castedBinding = binding as ListenerBinding<StateT, EventT, TransitionT>

              val eventContext = object : EventContext<StateT, TransitionT> {
                override suspend fun enterState(block: StateT.() -> TransitionT) {
                  val transition = getLatestState().block()
                  transitions.send(transition)
                }

                override suspend fun getLatestState() = getLatestState()
              }
              castedBinding.listener(eventContext, event)
            }
          }
        }
    }
  }

  private fun CoroutineScope.hookUpExternalFlows(
    getLatestState: () -> StateT,
    transitions: SendChannel<TransitionT>,
    externalFlowBindings: List<ExternalFlowBinding<StateT, TransitionT>>
  ) {
    val eventCtx = object : EventContext<StateT, TransitionT> {
      override suspend fun enterState(block: StateT.() -> TransitionT) {
        val transition = block(getLatestState())
        transitions.send(transition)
      }

      override suspend fun getLatestState() = getLatestState()
    }
    val flowCtx = object : FlowContext<StateT, TransitionT> {
      override suspend fun <T> Flow<T>.hookUp(block: suspend EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription {
        collect {
          block(eventCtx, it)
        }
        return HookedUpSubscription()
      }
    }
    for (binding in externalFlowBindings) {
      launch {
        binding.block(flowCtx)
      }
    }
  }
}

class StateMachineBuilder<StateT, EventT, TransitionT> {
  @PublishedApi
  internal val eventBindings =
    mutableMapOf<Class<out EventT>, MutableList<ListenerBinding<StateT, out EventT, TransitionT>>>()

  @PublishedApi
  internal val externalFlowBindings = mutableListOf<ExternalFlowBinding<StateT, TransitionT>>()

  inline fun <reified T : EventT> on(noinline block: suspend EventContext<StateT, TransitionT>.(T) -> Unit) {
    val bindings = eventBindings.getOrPut(T::class.java) { mutableListOf() }
    bindings.add(ListenerBinding(block))
  }

  fun externalFlow(block: suspend FlowContext<StateT, TransitionT>.() -> HookedUpSubscription) {
    val binding = ExternalFlowBinding(block)
    externalFlowBindings.add(binding)
  }
}

class ListenerBinding<StateT, SpecificIntentT, TransitionT>(
  val listener: suspend EventContext<StateT, TransitionT>.(SpecificIntentT) -> Unit,
  val distinct: Boolean = false
)

class ExternalFlowBinding<StateT, TransitionT>(
  val block: suspend FlowContext<StateT, TransitionT>.() -> HookedUpSubscription
)


interface EventContext<StateT, TransitionT> {
  suspend fun enterState(block: StateT.() -> TransitionT)
  suspend fun getLatestState(): StateT
}

/** Used to enforce usage of `hookUp`. */
class HookedUpSubscription internal constructor()

interface FlowContext<StateT, TransitionT> {
  suspend fun <T> Flow<T>.hookUp(block: suspend EventContext<StateT, TransitionT>.(T) -> Unit = {}): HookedUpSubscription
}
