package shronq.statemachine

import alter.test.runTest
import com.google.common.truth.Truth.assertThat
import com.squareup.sqldelight.runtime.coroutines.test
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
  private val eventsChannel = Channel<TestEvent>(UNLIMITED)
  private val testCoroutineContext = TestCoroutineContext()
  private val job = Job()
  private val scope = CoroutineScope(job)

  private fun engine(initializer: EngineContext<TestState, TestEvent, TestTransition>.() -> Unit) = flow {
    val engine = StateMachine.createSimple(
      coroutineScope = scope,
      initialState = TestState.initial(),
      events = events.asFlow(),
      initializer = initializer
    )
    engine.start()
    testCoroutineContext.triggerActions()
    engine.states.collect {
      emit(it)
    }
  }

  private fun engine2(initializer: EngineContext<TestState, TestEvent, TestTransition>.() -> Unit) = flow {
    val eventsFlow = flow {
      eventsChannel.consumeEach { emit(it) }
    }
    val engine = StateMachine.createSimple(
      coroutineScope = scope,
      initialState = TestState.initial(),
      events = eventsFlow,
      initializer = initializer
    )
    engine.start()
    testCoroutineContext.triggerActions()
    engine.states.collect {
      emit(it)
    }
  }

  @Test fun `starts with initial state`() {
    val states = engine { }
    TODO("Assertions")
//    states.assertValues(TestState.initial())
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

  @Test fun `event triggers new state`() = runTest {
    val states = engine2 {
      on<CountUp> {
        enterState { state.incrementCounter() }
      }

      on<CountDown> {
        enterState { state.decrementCounter() }
      }
    }

    states.test {
      eventsChannel.send(CountUp)
      println(item())
      eventsChannel.send(CountUp)
      delay(500)
      println(item())
      eventsChannel.send(CountUp)
      println(item())
      eventsChannel.send(CountDown)
      println(item())

      val events = Array(5) { item() }
      assertThat(events.size).isEqualTo(5)
      assertThat(events.last()).isEqualTo(TestState(2, 0))
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
        onDistinct<TestEvent.Add> {
          setReceived++
        }
      }
      events.onNext(TestEvent.Add(1))
      events.onNext(TestEvent.Add(1))
      events.onNext(TestEvent.Add(2))
      events.onNext(TestEvent.Add(2))
      events.onNext(TestEvent.Add(3))
      events.onNext(TestEvent.Add(4))
      testCoroutineContext.triggerActions()
      assertThat(setReceived).isEqualTo(4)
    }
  }

  @Test fun `flowOf works`() {
    runBlocking {
      var countUpReceivedFromFlowOf = 0
      var hookUpBlockCalled = 0
      engine {
        flowOf<CountUp> { events ->
          events
            .onEach { countUpReceivedFromFlowOf++ }
            .hookUp {
              hookUpBlockCalled++
            }
        }
      }

      events.onNext(CountUp)
      events.onNext(CountUp)
      testCoroutineContext.triggerActions()

      assertThat(countUpReceivedFromFlowOf).isEqualTo(2)
      assertThat(hookUpBlockCalled).isEqualTo(2)
    }
  }

  @Test fun `externals work`() {
    runBlocking {
      val times = Channel<Int>(10)
      val timesFlow = flow {
        times.consumeEach { emit(it) }
      }

      val states = engine {
        externalFlow {
          timesFlow
            .map { it * 2 }
            .hookUp {
              enterState { state.addSeconds(it) }
            }
        }
      }

      times.send(1)
      times.send(2)
      testCoroutineContext.triggerActions()

      TODO("Assertions")
//      states.assertValues(
//        TestState.initial(),
//        TestState.initial().copy(secondsSum = 2),
//        TestState.initial().copy(secondsSum = 6)
//      )
    }
  }

  @Ignore("remodel to check that job is cancelled?")
  @Test fun `parameterless hookUp is unsubscribed when job is cancelled`() {
  }

  @Ignore("remodel to check that job is cancelled?")
  @Test fun `hookup with block is unsubscribed when job is cancelled`() {
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
      TODO("Assertions")
//      states.assertValues(
//        TestState(counter = 0),
//        TestState(counter = 1),
//        TestState(counter = 2),
//        TestState(counter = 3)
//      )
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
