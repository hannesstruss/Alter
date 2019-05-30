package shronq.statemachine

sealed class TestEvent {
  data class Add(val i: Int) : TestEvent()
  object CountUp : TestEvent()
  object CountDown : TestEvent()
}
