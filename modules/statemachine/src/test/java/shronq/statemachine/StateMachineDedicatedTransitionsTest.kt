package shronq.statemachine

import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineContext
import org.junit.Test

private sealed class DedicatedTransition {
  data class Add(val i: Int) : DedicatedTransition()
}

@FlowPreview
@ObsoleteCoroutinesApi
class StateMachineDedicatedTransitionsTest {
  private val events = PublishSubject.create<TestEvent>()
  private val testCoroutineContext = TestCoroutineContext()
  private val job = Job()
  private val scope = CoroutineScope(testCoroutineContext + job)

  @Test fun `accepts complex transitions`() {
    runBlocking {
      val applyTransition = { state: TestState, transition: DedicatedTransition ->
        when (transition) {
          is DedicatedTransition.Add -> state.copy(counter = state.counter + transition.i)
        }
      }
      val machine = StateMachine.create<TestState, TestEvent, DedicatedTransition>(
          coroutineScope = scope,
          initialState = TestState.initial(),
          events = events,
          applyTransition = applyTransition
      ) {
        on<TestEvent.CountUp> {
          enterState { DedicatedTransition.Add(2) }
        }
      }
      val states = machine.states.test()
      machine.start()
      testCoroutineContext.triggerActions()

      events.onNext(TestEvent.CountUp)
      testCoroutineContext.triggerActions()

      states.assertValues(TestState.initial(), TestState.initial().copy(counter = 2))
    }
  }
}
