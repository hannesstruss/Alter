package de.hannesstruss.alter.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import de.hannesstruss.alter.R
import de.hannesstruss.alter.di.ActivityFinder
import javax.inject.Inject

class AACNavigator
@Inject constructor(
  private val activityFinder: ActivityFinder
) : Navigator {
  override fun navigateTo(destination: NavDirections) {
    navController().navigate(destination)
  }

  override fun back() {
    navController().popBackStack()
  }

  private fun navController(): NavController {
    val activity = activityFinder.findActivity()
    val navController = activity.findNavController(R.id.nav_host_fragment)
    return navController
  }
}
