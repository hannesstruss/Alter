package de.hannesstruss.alter.di

import android.content.Context
import de.hannesstruss.alter.AlterApp
import de.hannesstruss.alter.features.list.BabyListDependencies

interface FeatureDependencies : BabyListDependencies {
  companion object {
    fun from(context: Context) = (context.applicationContext as AlterApp).appComponent as FeatureDependencies
  }
}
