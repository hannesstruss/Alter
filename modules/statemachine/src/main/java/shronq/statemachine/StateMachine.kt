package shronq.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

// TODO: Nested engines?

class StateMachine<StateT : Any, EventT : Any, TransitionT : Any>
private constructor(
  private val coroutineScope: CoroutineScope,
  private val initialState: StateT,
  private val events: Flow<EventT>,
  private val applyTransition: (StateT, TransitionT) -> StateT,
  private val initializer: EngineContext<StateT, EventT, TransitionT>.() -> Unit
) {
  companion object {
    fun <StateT : Any, EventT : Any, TransitionT : Any> create(
      coroutineScope: CoroutineScope,
      initialState: StateT,
      events: Flow<EventT>,
      applyTransition: (StateT, TransitionT) -> StateT,
      initializer: EngineContext<StateT, EventT, TransitionT>.() -> Unit
    ): StateMachine<StateT, EventT, TransitionT> = StateMachine(
      coroutineScope = coroutineScope,
      initialState = initialState,
      events = events,
      applyTransition = applyTransition,
      initializer = initializer
    )

    fun <StateT : Any, EventT : Any> createSimple(
      coroutineScope: CoroutineScope,
      initialState: StateT,
      events: Flow<EventT>,
      initializer: EngineContext<StateT, EventT, StateT>.() -> Unit
    ): StateMachine<StateT, EventT, StateT> = StateMachine(
      coroutineScope = coroutineScope,
      initialState = initialState,
      events = events,
      applyTransition = { _, nextState -> nextState },
      initializer = initializer
    )
  }

  private val transitionProducers =
    Channel<StateEditorContext<StateT>.() -> TransitionT>(UNLIMITED)

  private val eventContext = object : EventContext<StateT, TransitionT> {
    override suspend fun enterState(block: StateEditorContext<StateT>.() -> TransitionT) {
      transitionProducers.send(block)
    }

    override suspend fun getLatestState(): StateT {
      return _states.value
    }
  }
  private val _states = ConflatedBroadcastChannel(initialState)
  val states
    get() = flow {
      _states.consumeEach {
        emit(it)
      }
    }

  init {
//    val connectableStates = transitionProducers
//      .scan(initialState) { state, transitionProducer ->
//        val ctx = object : StateEditorContext<StateT> {
//          override val state = state
//        }
//
//        applyTransition(state, ctx.transitionProducer())
//      }
//      .replay(1)
//
//    // Immediately subscribe to catch all states in the replay. There's no chance
//    // of upstream resources to leak or errors to be emitted, so that's okay.
//    // TODO: There's actually a chance of throwing an error from the transition lambda D:
//    connectableStates.autoConnect(0)
//
//    states = connectableStates


  }

  // TODO make states a cold stream to get rid of start/dispose?
  fun start() {
    val ctx = EngineContext<StateT, EventT, TransitionT>()
    ctx.initializer()

    for (binding in ctx.eventBindings) {
      @Suppress("UNCHECKED_CAST")
      val casted = binding as ListenerBinding<StateT, EventT, TransitionT>
      coroutineScope.launch {
        var filtered = events.filter { it.javaClass == casted.eventClass }
        if (casted.distinct) {
          filtered = filtered.distinctUntilChanged()
        }
        filtered.collect { event ->
          casted.listener(eventContext, event)
        }
      }
    }

    for (binding in ctx.firstEventBindings) {
      coroutineScope.launch {
        events
          .filter { it.javaClass == binding.eventClass }
          .take(1)
          .collect { event ->
            @Suppress("UNCHECKED_CAST")
            val casted = binding as ListenerBinding<StateT, EventT, TransitionT>
            casted.listener(eventContext, event)
          }
      }
    }

    val streamContext = object : FlowContext<StateT, TransitionT> {
      override suspend fun <T> Flow<T>.hookUp(): HookedUpSubscription {
        return hookUpInternal(null)
      }

      override suspend fun <T> Flow<T>.hookUp(block: suspend EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription {
        return hookUpInternal(block)
      }

      private fun <T> Flow<T>.hookUpInternal(block: (suspend EventContext<StateT, TransitionT>.(T) -> Unit)?): HookedUpSubscription {
        coroutineScope.launch {
          this@hookUpInternal.collect { block?.invoke(eventContext, it) }
        }
        return HookedUpSubscription()
      }
    }

    for (binding in ctx.flowBindings) {
      val filteredEvents = events.filter { it.javaClass == binding.eventClass }
      @Suppress("UNCHECKED_CAST")
      val casted = binding as FlowBinding<StateT, EventT, TransitionT>
      coroutineScope.launch {
        casted.block(streamContext, filteredEvents)
      }
    }

    val flowContext = object : FlowContext<StateT, TransitionT> {
      override suspend fun <T> Flow<T>.hookUp(): HookedUpSubscription {
        coroutineScope.launch {
          collect { }
        }
        return HookedUpSubscription()
      }

      override suspend fun <T> Flow<T>.hookUp(block: suspend EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription {
        coroutineScope.launch {
          collect { block(eventContext, it) }
        }
        return HookedUpSubscription()
      }
    }

    coroutineScope.launch {
      for (binding in ctx.externalFlowBindings) {
        binding.block(flowContext)
      }
    }

    coroutineScope.launch {
      ctx.onInitCallback?.invoke(eventContext)
    }

    coroutineScope.launch {
      var lastState = initialState

      transitionProducers.consumeEach { transitionProducer ->
        val stateEditorContext = object : StateEditorContext<StateT> {
          override val state = lastState
        }

        val nextState = applyTransition(lastState, stateEditorContext.transitionProducer())
        _states.send(nextState)
        lastState = nextState
      }
    }
  }
}

@FlowPreview
class EngineContext<StateT, EventT, TransitionT> internal constructor() {
  @PublishedApi
  internal val eventBindings = mutableListOf<ListenerBinding<StateT, out EventT, TransitionT>>()
  @PublishedApi
  internal val firstEventBindings =
    mutableListOf<ListenerBinding<StateT, out EventT, TransitionT>>()
  @PublishedApi
  internal val flowBindings = mutableListOf<FlowBinding<StateT, out EventT, TransitionT>>()
  @PublishedApi
  internal val externalFlowBindings = mutableListOf<ExternalBinding<StateT, TransitionT>>()

  internal var onInitCallback: (suspend EventContext<StateT, TransitionT>.() -> Unit)? = null

  fun onInit(block: suspend EventContext<StateT, TransitionT>.() -> Unit) {
    onInitCallback = block
  }

  inline fun <reified T : EventT> on(noinline block: suspend EventContext<StateT, TransitionT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block)
    eventBindings.add(binding)
  }

  inline fun <reified T : EventT> onDistinct(noinline block: suspend EventContext<StateT, TransitionT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block, distinct = true)
    eventBindings.add(binding)
  }

  inline fun <reified T : EventT> onFirst(noinline block: suspend EventContext<StateT, TransitionT>.(T) -> Unit) {
    val binding = ListenerBinding(T::class.java, block)
    firstEventBindings.add(binding)
  }

  inline fun <reified T : EventT> flowOf(noinline block: suspend FlowContext<StateT, TransitionT>.(Flow<T>) -> HookedUpSubscription) {
    val binding = FlowBinding(T::class.java, block)
    flowBindings.add(binding)
  }

  fun externalFlow(block: suspend FlowContext<StateT, TransitionT>.() -> HookedUpSubscription) {
    val binding = ExternalBinding(block)
    externalFlowBindings.add(binding)
  }
}

class ListenerBinding<StateT, SpecificIntentT, TransitionT>(
  val eventClass: Class<out SpecificIntentT>,
  val listener: suspend EventContext<StateT, TransitionT>.(SpecificIntentT) -> Unit,
  val distinct: Boolean = false
)

interface EventContext<StateT, TransitionT> {
  suspend fun enterState(block: StateEditorContext<StateT>.() -> TransitionT)
  suspend fun getLatestState(): StateT
}

interface StateEditorContext<StateT> {
  val state: StateT
}

interface FlowContext<StateT, TransitionT> {
  suspend fun <T> Flow<T>.hookUp(): HookedUpSubscription
  suspend fun <T> Flow<T>.hookUp(block: suspend EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription
}

/** Enforces usage of `StreamContext.hookUp`. */
class HookedUpSubscription internal constructor()

class ExternalBinding<StateT, TransitionT>(
  val block: suspend FlowContext<StateT, TransitionT>.() -> HookedUpSubscription
)

@FlowPreview
class FlowBinding<StateT, SpecificEventT, TransitionT>(
  val eventClass: Class<out SpecificEventT>,
  val block: suspend FlowContext<StateT, TransitionT>.(Flow<SpecificEventT>) -> HookedUpSubscription
)
