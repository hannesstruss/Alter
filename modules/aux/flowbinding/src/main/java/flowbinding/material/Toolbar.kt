package flowbinding.material

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Toolbar.itemClicks(): Flow<MenuItem> {
  return callbackFlow {
    setOnMenuItemClickListener { offer(it) }
    awaitClose { setOnMenuItemClickListener(null) }
  }
}
