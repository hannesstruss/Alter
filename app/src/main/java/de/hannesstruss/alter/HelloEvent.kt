package de.hannesstruss.alter

sealed class HelloEvent {
  object Click : HelloEvent()
}
