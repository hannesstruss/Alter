package shronq.statemachine

data class TestState(val counter: Int = 0, val secondsSum: Int = 0) {
  companion object {
    fun initial() = TestState()
  }

  fun incrementCounter() = copy(counter = counter + 1)
  fun decrementCounter() = copy(counter = counter - 1)
  fun addSeconds(seconds: Int) = copy(secondsSum = secondsSum + seconds)
}
