package de.hannesstruss.alter.navigation

import androidx.navigation.NavDirections

interface Navigator {
  fun navigateTo(destination: NavDirections)
  fun back()
}
