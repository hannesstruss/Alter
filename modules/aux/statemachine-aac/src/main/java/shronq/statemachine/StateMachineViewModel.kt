package shronq.statemachine

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class StateMachineViewModel<StateT, EventT : Any> : ViewModel(), CoroutineScope {
  private val job = Job()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  abstract val initialState: StateT
  // Use StateT as transition type to only describe a transition as its target state:
  abstract val stateMachine: StateMachine<StateT, EventT, StateT>

  private val views = ConflatedBroadcastChannel<Flow<EventT>>()
  private val viewEvents = channelFlow {
    views.asFlow().flatMapLatest { it }.collect {
      Timber.d("Got event in switchmap: $it")
      send(it)
    }
  }

  val states: Flow<StateT> get() = stateMachine.states

  fun attachView(events: Flow<EventT>) {
    views.offer(events)
  }

  fun dropView() {
    val never = flow<EventT> {}
    views.offer(never)
  }

  protected fun createEngine(block: StateMachineBuilder<StateT, EventT, StateT>.() -> Unit): StateMachine<StateT, EventT, StateT> {
    return StateMachine(
      initialState = initialState,
      events = viewEvents,
      applyTransition = { _, nextState -> nextState },
      block = block
    )
  }

  override fun onCleared() {
    super.onCleared()
    job.cancel()
  }
}
