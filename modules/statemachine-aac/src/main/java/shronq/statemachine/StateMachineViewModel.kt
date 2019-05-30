package shronq.statemachine

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

abstract class StateMachineViewModel<StateT : Any, IntentT : Any> : ViewModel(), CoroutineScope {
  private val job = Job()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  abstract val initialState: StateT
  // Use StateT as transition type to only describe a transition as its target state:
  abstract val stateMachine: StateMachine<StateT, IntentT, StateT>

  private val views = BehaviorRelay.create<Observable<IntentT>>()

  val intents: Observable<IntentT> = views.switchMap { it }
  val states: Flow<StateT> get() = stateMachine.states

  fun attachView(intents: Flow<IntentT>) {
    views.accept(intents)
  }

  fun dropView() {
    views.accept(Observable.never())
  }

  protected fun createEngine(block: StateMachineBuilder<StateT, IntentT, StateT>.() -> Unit): StateMachine<StateT, IntentT, StateT> {
    return StateMachine(
        initialState = initialState,
        events = intents,
        initializer = block
    )
  }

  override fun onCleared() {
    super.onCleared()
    job.cancel()
  }
}
