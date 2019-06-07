package de.hannesstruss.alter.flowextensions

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take

suspend fun <T> Flow<T>.awaitFirst(): T {
  var t: T? = null
  take(1).collect { t = it }
  return t!!
}
