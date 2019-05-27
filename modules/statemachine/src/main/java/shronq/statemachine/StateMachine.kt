package shronq.statemachine

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.awaitFirst
import kotlinx.coroutines.rx2.openSubscription

// TODO: Nested engines?

class StateMachine<StateT : Any, EventT : Any, TransitionT : Any>
private constructor(
    private val coroutineScope: CoroutineScope,
    initialState: StateT,
    private val events: Observable<out EventT>,
    private val applyTransition: (StateT, TransitionT) -> StateT,
    private val initializer: EngineContext<StateT, EventT, TransitionT>.() -> Unit
) {
  companion object {
    fun <StateT : Any, EventT : Any, TransitionT : Any> create(
        coroutineScope: CoroutineScope,
        initialState: StateT,
        events: Observable<out EventT>,
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
        events: Observable<out EventT>,
        initializer: EngineContext<StateT, EventT, StateT>.() -> Unit
    ): StateMachine<StateT, EventT, StateT> = StateMachine(
        coroutineScope = coroutineScope,
        initialState = initialState,
        events = events,
        applyTransition = { _, nextState -> nextState },
        initializer = initializer
    )
  }

  private val transitionProducers = PublishRelay.create<StateEditorContext<StateT>.() -> TransitionT>().toSerialized()
  private val eventContext = object : EventContext<StateT, TransitionT> {
    override fun enterState(block: StateEditorContext<StateT>.() -> TransitionT) {
      transitionProducers.accept(block)
    }

    override suspend fun getLatestState(): StateT {
      return states.awaitFirst()
    }
  }
  val states: Observable<StateT>

  init {
    val connectableStates = transitionProducers
        .scan(initialState) { state, transitionProducer ->
          val ctx = object : StateEditorContext<StateT> {
            override val state = state
          }

          applyTransition(state, ctx.transitionProducer())
        }
        .replay(1)

    // Immediately subscribe to catch all states in the replay. There's no chance
    // of upstream resources to leak or errors to be emitted, so that's okay.
    // TODO: There's actually a chance of throwing an error from the transition lambda D:
    connectableStates.autoConnect(0)

    states = connectableStates
  }

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
        filtered.openSubscription().consumeEach { event ->
          casted.listener(eventContext, event)
        }
      }
    }

    for (binding in ctx.firstEventBindings) {
      coroutineScope.launch {
        events
            .filter { it.javaClass == binding.eventClass }
            .firstElement()
            .openSubscription()
            .consumeEach { event ->
              @Suppress("UNCHECKED_CAST")
              val casted = binding as ListenerBinding<StateT, EventT, TransitionT>
              casted.listener(eventContext, event)
            }
      }
    }

    val streamContext = object : StreamContext<StateT, TransitionT> {
      override fun <T> Observable<T>.hookUp(): HookedUpSubscription {
        return hookUpInternal(null)
      }

      override fun <T> Observable<T>.hookUp(block: EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription {
        return hookUpInternal(block)
      }

      private fun <T> Observable<T>.hookUpInternal(block: (EventContext<StateT, TransitionT>.(T) -> Unit)?): HookedUpSubscription {
        coroutineScope.launch {
          this@hookUpInternal
              .openSubscription()
              .consumeEach { block?.invoke(eventContext, it) }
        }
        return HookedUpSubscription()
      }
    }

    for (binding in ctx.streamBindings) {
      val filteredEvents = events.filter { it.javaClass == binding.eventClass }
      @Suppress("UNCHECKED_CAST")
      val casted = binding as StreamBinding<StateT, EventT, TransitionT>
      casted.block(streamContext, filteredEvents)
    }

    for (binding in ctx.externalStreamBindings) {
      binding.block(streamContext)
    }

    val flowContext = object : FlowContext<StateT, TransitionT> {
      override fun <T> Flow<T>.hookUp(): HookedUpSubscription {
        coroutineScope.launch {
          collect { }
        }
        return HookedUpSubscription()
      }

      override fun <T> Flow<T>.hookUp(block: EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription {
        coroutineScope.launch {
          collect { block(eventContext, it) }
        }
        return HookedUpSubscription()
      }
    }

    for (binding in ctx.externalFlowBindings) {
      binding.block(flowContext)
    }

    coroutineScope.launch {
      ctx.onInitCallback?.invoke(eventContext)
    }
  }
}

@FlowPreview
class EngineContext<StateT, EventT, TransitionT> internal constructor() {
  @PublishedApi
  internal val eventBindings = mutableListOf<ListenerBinding<StateT, out EventT, TransitionT>>()
  @PublishedApi
  internal val firstEventBindings = mutableListOf<ListenerBinding<StateT, out EventT, TransitionT>>()
  @PublishedApi
  internal val streamBindings = mutableListOf<StreamBinding<StateT, out EventT, TransitionT>>()
  @PublishedApi
  internal val externalStreamBindings = mutableListOf<ExternalBinding<StateT, TransitionT>>()
  @PublishedApi
  internal val externalFlowBindings = mutableListOf<FlowBinding<StateT, TransitionT>>()

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

  inline fun <reified T : EventT> streamOf(noinline block: StreamContext<StateT, TransitionT>.(Observable<out T>) -> HookedUpSubscription) {
    val binding = StreamBinding(T::class.java, block)
    streamBindings.add(binding)
  }

  fun externalStream(block: StreamContext<StateT, TransitionT>.() -> HookedUpSubscription) {
    val binding = ExternalBinding(block)
    externalStreamBindings.add(binding)
  }

  fun externalFlow(block: FlowContext<StateT, TransitionT>.() -> HookedUpSubscription) {
    val binding = FlowBinding(block)
    externalFlowBindings.add(binding)
  }
}

class ListenerBinding<StateT, SpecificIntentT, TransitionT>(
    val eventClass: Class<out SpecificIntentT>,
    val listener: suspend EventContext<StateT, TransitionT>.(SpecificIntentT) -> Unit,
    val distinct: Boolean = false
)

class StreamBinding<StateT, SpecificIntentT, TransitionT>(
    val eventClass: Class<out SpecificIntentT>,
    val block: StreamContext<StateT, TransitionT>.(Observable<out SpecificIntentT>) -> HookedUpSubscription
)

interface EventContext<StateT, TransitionT> {
  fun enterState(block: StateEditorContext<StateT>.() -> TransitionT)
  suspend fun getLatestState(): StateT
}

interface StateEditorContext<StateT> {
  val state: StateT
}

interface StreamContext<StateT, TransitionT> {
  fun <T> Observable<T>.hookUp(): HookedUpSubscription
  fun <T> Observable<T>.hookUp(block: EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription
}

@FlowPreview
interface FlowContext<StateT, TransitionT> {
  fun <T> Flow<T>.hookUp(): HookedUpSubscription
  fun <T> Flow<T>.hookUp(block: EventContext<StateT, TransitionT>.(T) -> Unit): HookedUpSubscription
}

/** Enforces usage of `StreamContext.hookUp`. */
class HookedUpSubscription internal constructor()

class ExternalBinding<StateT, TransitionT>(
    val block: StreamContext<StateT, TransitionT>.() -> HookedUpSubscription
)

@FlowPreview
class FlowBinding<StateT, TransitionT>(
    val block: FlowContext<StateT, TransitionT>.() -> HookedUpSubscription
)
