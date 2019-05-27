package shronq.statemachine

import com.google.common.truth.Truth.assertThat
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineContext
import org.junit.Ignore
import org.junit.Test
import shronq.statemachine.TestEvent.CountDown
import shronq.statemachine.TestEvent.CountUp

// Instead of modeling a distinct transition, we just describe the target state.
private typealias TestTransition = TestState

@FlowPreview
@ObsoleteCoroutinesApi
class StateMachineTest {

  private val events = PublishSubject.create<TestEvent>()
  private val testCoroutineContext = TestCoroutineContext()
  private val job = Job()
  private val scope = CoroutineScope(testCoroutineContext + job)

  private fun engine(initializer: EngineContext<TestState, TestEvent, TestTransition>.() -> Unit): TestObserver<TestState> {
    val engine = StateMachine.createSimple(
        coroutineScope = scope,
        initialState = TestState.initial(),
        events = events,
        initializer = initializer
    )
    val test = engine.states.test()
    engine.start()
    testCoroutineContext.triggerActions()
    return test
  }

  @Test fun `starts with initial state`() {
    val states = engine { }
    states.assertValues(TestState.initial())
  }

  @Test fun `calls onInit`() {
    var onInitCalled = 0
    engine {
      onInit {
        onInitCalled++
      }
    }
    testCoroutineContext.triggerActions()
    assertThat(onInitCalled).isEqualTo(1)
  }

  @Test fun `events are unsubscribed from when job is cancelled`() {
    runBlocking {
      engine {
        on<CountUp> {
          // nothing
        }

        onFirst<CountDown> {
          // nothing
        }
      }
      assertThat(events.hasObservers()).isTrue()
      job.cancel()
      testCoroutineContext.triggerActions()
      assertThat(events.hasObservers()).isFalse()
    }
  }

  @Test fun `event triggers new state`() {
    runBlocking {
      val states = engine {
        on<CountUp> {
          enterState { state.incrementCounter() }
        }

        on<CountDown> {
          enterState { state.decrementCounter() }
        }
      }
      events.onNext(CountUp)
      events.onNext(CountUp)
      events.onNext(CountUp)
      events.onNext(CountDown)
      testCoroutineContext.triggerActions()
      states.assertValueCount(5)
      assertThat(states.values().last()).isEqualTo(TestState(2, 0))
    }
  }

  @Test fun `errors from event handlers are relayed`() {
    val e = RuntimeException("Hello")

    engine {
      on<CountUp> {
        throw e
      }
    }
    events.onNext(CountUp)
    testCoroutineContext.triggerActions()

    assertThat(testCoroutineContext.exceptions).containsExactly(e)
  }

  @Test fun `onFirst works`() {
    runBlocking {
      var countUpReceivedFromOnFirst = 0
      engine {
        onFirst<CountUp> {
          countUpReceivedFromOnFirst++
        }
      }
      events.onNext(CountUp)
      events.onNext(CountUp)
      testCoroutineContext.triggerActions()
      assertThat(countUpReceivedFromOnFirst).isEqualTo(1)
    }
  }

  @Test fun `onDistinct works`() {
    runBlocking {
      var setReceived = 0
      engine {
        onDistinct<TestEvent.Set> {
          setReceived++
        }
      }
      events.onNext(TestEvent.Set(1))
      events.onNext(TestEvent.Set(1))
      events.onNext(TestEvent.Set(2))
      events.onNext(TestEvent.Set(2))
      events.onNext(TestEvent.Set(3))
      events.onNext(TestEvent.Set(4))
      testCoroutineContext.triggerActions()
      assertThat(setReceived).isEqualTo(4)
    }
  }

  @Test fun `streamOf works`() {
    runBlocking {
      var countUpReceivedFromStreamOf = 0
      var hookUpBlockCalled = 0
      engine {
        streamOf<CountUp> { events ->
          events
              .doOnNext { countUpReceivedFromStreamOf++ }
              .hookUp {
                hookUpBlockCalled++
              }
        }
      }

      events.onNext(CountUp)
      events.onNext(CountUp)
      testCoroutineContext.triggerActions()

      assertThat(countUpReceivedFromStreamOf).isEqualTo(2)
      assertThat(hookUpBlockCalled).isEqualTo(2)
    }
  }

  @Test fun `externals work`() {
    runBlocking {
      val times = PublishSubject.create<Int>()

      val states = engine {
        externalStream {
          times
              .map { it * 2 }
              .hookUp {
                enterState { state.addSeconds(it) }
              }
        }
      }

      times.onNext(1)
      times.onNext(2)
      testCoroutineContext.triggerActions()

      states.assertValues(TestState.initial(), TestState.initial().copy(secondsSum = 2), TestState.initial().copy(secondsSum = 6))
    }
  }

  @Test fun `parameterless hookUp is unsubscribed when job is cancelled`() {
    val external = PublishSubject.create<Int>()
    engine {
      externalStream {
        external.hookUp()
      }
    }
    assertThat(external.hasObservers()).isTrue()
    job.cancel()
    testCoroutineContext.triggerActions()
    assertThat(external.hasObservers()).isFalse()
  }

  @Test fun `hookup with block is unsubscribed when job is cancelled`() {
    val external = PublishSubject.create<Int>()
    engine {
      externalStream {
        external.hookUp {
          // nothing
        }
      }
    }
    assertThat(external.hasObservers()).isTrue()
    job.cancel()
    testCoroutineContext.triggerActions()
    assertThat(external.hasObservers()).isFalse()
  }

  @Test fun `externalFlow works`() {
    runBlocking {
      val states = engine {
        externalFlow {
          flowOf(1, 2, 3).hookUp { flowItem ->
            enterState { state.copy(counter = flowItem) }
          }
        }
      }
      states.assertValues(
          TestState(counter = 0),
          TestState(counter = 1),
          TestState(counter = 2),
          TestState(counter = 3)
      )
    }
  }

  // TODO
  @Ignore("Implement") @Test fun `state transitions`() {
    engine {
      //      onTransition<PrefState, NextState> {
//
//      }
    }
  }

  // TODO
  @Ignore("Implement") @Test fun `on enter state`() {
    engine {
      //      onEnterState<NextState>() {
//        call repeatedly on same state class?
//      }

//      onEnterState {
//        any state
//      }
    }
  }
}
