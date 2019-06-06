package de.hannesstruss.alter.features.common

import android.annotation.SuppressLint
import shronq.statemachine.StateMachineFragment
import shronq.statemachine.StateMachineViewModel

abstract class FeatureDependencyProvidingFragment
<StateT, EventT : Any, ViewModelT : StateMachineViewModel<StateT, EventT>> : StateMachineFragment<StateT, EventT, ViewModelT>() {
  @SuppressLint("WrongConstant")
  @Suppress("UNCHECKED_CAST")
  override fun <T> getFeatureDependencies(): T {
    return requireContext().getSystemService(FeatureDependenciesServiceName) as T
  }
}
