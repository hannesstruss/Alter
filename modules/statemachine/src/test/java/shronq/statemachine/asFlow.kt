package shronq.statemachine

import io.reactivex.Observable
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx2.openSubscription

internal fun <T> Observable<T>.asFlow() = flow {
  val channel = openSubscription()
  channel.consumeEach { emit(it) }
}
