package shronq.statemachine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class StateMachineFragment
<StateT, EventT : Any, ViewModelT : StateMachineViewModel<StateT, EventT>> : Fragment() {
  @get:LayoutRes abstract val layout: Int
  abstract val viewModelClass: Class<ViewModelT>
  abstract fun events(): Flow<EventT>
  abstract fun render(state: StateT)

  private var renderJob = Job()
  private val renderScope = object : CoroutineScope {
    override val coroutineContext get() = Dispatchers.Main + renderJob
  }

  private lateinit var viewModel: ViewModelT

  @SuppressLint("WrongConstant")
  private fun initViewModel(context: Context) {
    val factory = context.getSystemService(ViewModelFactory.SERVICE_NAME) as ViewModelFactory
    viewModel = ViewModelProviders.of(this, factory).get(viewModelClass)
  }

  @CallSuper
  override fun onAttach(context: Context) {
    initViewModel(context)
    super.onAttach(context)
  }

  @CallSuper
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(layout, container, false)
  }

  @CallSuper
  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)

    // TODO: first render called after restore, which resets recycler view scroll positions etc.

    renderJob = Job()
    renderScope.launch {
      viewModel.states.collect { render(it) }
    }
    viewModel.attachView(events())
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()

    renderJob.cancel()
    viewModel.dropView()
  }
}
