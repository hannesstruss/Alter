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
  private fun adapter(
    block: StateMachineBuilder<TestState, TestEvent, TestState>.() -> Unit
  ): StateMachineTestAdapter<TestState, TestEvent, TestState> {
    return StateMachineTestAdapter(
      initialState = TestState.initial(),
      block = block,
      applyTransition = { _, nextState -> nextState }
    )
  }

  @Test fun `handles simple events and state transitions`() = runTest {
    val machine = adapter {
      on<CountUp> {
        enterState { copy(counter = counter + 1) }
      }

      on<CountDown> {
        enterState { copy(counter = counter - 1) }
      }
    }
    machine.states.test {
      val first = item()
      assertThat(first).isEqualTo(TestState.initial())

      machine.send(CountUp)
      assertThat(item().counter).isEqualTo(1)

      machine.send(CountUp)
      assertThat(item().counter).isEqualTo(2)

      machine.send(CountDown)
      assertThat(item().counter).isEqualTo(1)

      cancel()
    }
  }

  @Test fun `handles external flows`() = runTest {
    val external = BroadcastChannel<Int>(10)

    val machine = adapter {
      externalFlow {
        flow {
          external.consumeEach { emit(it) }
        }.hookUp {
          enterState { copy(counter = it) }
        }
      }
    }

    machine.states.test {
      item() // Ignore first.

      external.send(1337)

      assertThat(item().counter).isEqualTo(1337)

      cancel()
    }
  }
}
