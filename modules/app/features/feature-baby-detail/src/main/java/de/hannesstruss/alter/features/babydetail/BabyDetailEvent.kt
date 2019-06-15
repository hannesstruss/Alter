package de.hannesstruss.alter.features.babydetail

sealed class BabyDetailEvent {
  object CycleThroughAgeFormats : BabyDetailEvent()
}
