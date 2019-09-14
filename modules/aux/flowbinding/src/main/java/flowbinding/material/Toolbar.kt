package flowbinding.material

import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter

fun Toolbar.itemClicks(): Flow<MenuItem> {
  return callbackFlow {
    setOnMenuItemClickListener { offer(it) }
    awaitClose { setOnMenuItemClickListener(null) }
  }
}

fun Toolbar.itemClicks(@IdRes id: Int) = itemClicks().filter { it.itemId == id }
