package de.hannesstruss.alter.navigation

import androidx.annotation.IdRes
import androidx.navigation.findNavController
import de.hannesstruss.alter.R
import de.hannesstruss.alter.di.ActivityFinder
import javax.inject.Inject

class AACNavigator
@Inject constructor(
  private val activityFinder: ActivityFinder
) : Navigator {
  override fun navigateTo(destination: Destination) {
    val activity = activityFinder.findActivity()
    val navController = activity.findNavController(R.id.nav_host_fragment)
    navController.navigate(action(destination))
  }

  @IdRes private fun action(destination: Destination): Int = when (destination) {
    Destination.BabyList -> TODO()
    is Destination.BabyDetail -> R.id.action_babyListFragment_to_babyDetailFragment
  }
}
