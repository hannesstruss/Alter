package de.hannesstruss.alter.features.common

import android.annotation.SuppressLint
import androidx.viewbinding.ViewBinding
import shronq.statemachine.StateMachineFragment
import shronq.statemachine.StateMachineViewModel

abstract class FeatureDependencyProvidingFragment
<StateT, EventT : Any, ViewModelT : StateMachineViewModel<StateT, EventT>, ViewBindingT : ViewBinding> : StateMachineFragment<StateT, EventT, ViewModelT, ViewBindingT>() {
  @SuppressLint("WrongConstant")
  @Suppress("UNCHECKED_CAST")
  override fun <T> getFeatureDependencies(): T {
    return requireContext().getSystemService(FeatureDependenciesServiceName) as T
  }
}
