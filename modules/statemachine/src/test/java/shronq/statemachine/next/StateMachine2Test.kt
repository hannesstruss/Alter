package shronq.statemachine.next

import alter.test.runTest
import com.google.common.truth.Truth.assertThat
import com.squareup.sqldelight.runtime.coroutines.test
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.flow
import org.junit.Test
import shronq.statemachine.TestEvent
import shronq.statemachine.TestEvent.CountDown
import shronq.statemachine.TestEvent.CountUp
import shronq.statemachine.TestState

class StateMachine2Test {
  private val events = BroadcastChannel<TestEvent>(100)

  private fun machine(): StateMachine2<TestState, TestEvent, TestState> {
    val machine = StateMachine2<TestState, TestEvent, TestState>(
      initialState = TestState.initial(),
      events = flow { events.consumeEach { emit(it) } },
      applyTransition = { _, nextState -> nextState }
    ) {
      on<CountUp> {
        println("Got a count up")
        enterState { copy(counter = counter + 1) }
      }

      on<CountDown> {
        println("Got a count down")
        enterState { copy(counter = counter - 1) }
      }
    }

    return machine
  }

  @Test fun `counts up and down`() = runTest {
    machine().states.test {
      println("Checking first...")
      val first = item()
      assertThat(first).isEqualTo(TestState.initial())
      println("Got first: $first")

      events.send(CountUp)
      assertThat(item().counter).isEqualTo(1)

      events.send(CountUp)
      assertThat(item().counter).isEqualTo(2)

      events.send(CountDown)
      assertThat(item().counter).isEqualTo(1)

      cancel()
    }
  }
}
