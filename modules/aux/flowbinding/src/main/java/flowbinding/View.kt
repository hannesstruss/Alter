package flowbinding

import android.view.View
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel

fun View.clicks(): Flow<Unit> = flowViaChannel { channel ->
  setOnClickListener { channel.offer(Unit) }
  channel.invokeOnClose { setOnClickListener(null) }
}
