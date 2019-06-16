package de.hannesstruss.alter.flowextensions

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

suspend fun <T> Flow<T>.awaitFirst(): T {
  var t: T? = null
  take(1).collect { t = it }
  return t!!
}

fun <T> mergeFlows(vararg flows: Flow<T>): Flow<T> = channelFlow {
  coroutineScope {
    for (f in flows) {
      launch {
        f.collect { channel.send(it) }
      }
    }
  }
}
