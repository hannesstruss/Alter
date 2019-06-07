package de.hannesstruss.alter.di

import androidx.fragment.app.FragmentActivity

interface ActivityFinder {
  fun findActivity(): FragmentActivity
}
