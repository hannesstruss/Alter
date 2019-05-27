package shronq.statemachine

sealed class TestEvent {
  data class Set(val i: Int) : TestEvent()
  object CountUp : TestEvent()
  object CountDown : TestEvent()
}
