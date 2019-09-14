package shronq.statemachine

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

interface FeatureComponent<ViewModelT> {
  fun viewModel(): ViewModelT
}

abstract class StateMachineFragment
<StateT, EventT : Any, ViewModelT : StateMachineViewModel<StateT, EventT>, ViewBindingT : ViewBinding> : Fragment() {
  abstract val bindingInflater: (inflater: LayoutInflater, parent: ViewGroup?, attachToRoot: Boolean) -> ViewBindingT
  /**
   * Kotlin can't infer the type of a method reference when it has overloads.
   * Use this to define [bindingInflater] without specifying the type again:
   *
   *     override val bindingInflater = infer(MyBinding::inflate)
   */
  protected fun infer(inflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBindingT) = inflater
  private var views: ViewBindingT? = null

  abstract val viewModelClass: Class<ViewModelT>
  abstract fun ViewBindingT.events(): Flow<EventT>
  abstract fun ViewBindingT.render(state: StateT)

  private var renderJob = Job()
  private val renderScope = object : CoroutineScope {
    override val coroutineContext get() = Dispatchers.Main + renderJob
  }

  private lateinit var viewModel: ViewModelT

  abstract fun <T> getFeatureDependencies(): T
  abstract fun createFeatureComponent(): FeatureComponent<ViewModelT>

  @SuppressLint("WrongConstant")
  private fun initViewModel() {
    val factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass != viewModelClass) {
          throw IllegalStateException("${modelClass.name} requested, but factory only knows ${viewModelClass.name}")
        }

        @Suppress("UNCHECKED_CAST")
        return createFeatureComponent().viewModel() as T
      }
    }


    viewModel = ViewModelProviders.of(this, factory).get(viewModelClass)
  }

  @CallSuper
  override fun onAttach(context: Context) {
    initViewModel()
    super.onAttach(context)
  }

  final override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = bindingInflater(inflater, container, false)
    views = binding
    return binding.root
  }

  final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (views ?: throw AssertionError()).onViewBound()
  }

  open fun ViewBindingT.onViewBound() {
  }

  @CallSuper
  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)

    // TODO: first render called after restore, which resets recycler view scroll positions etc.

    renderJob = Job()
    renderScope.launch {
      viewModel.states.collect { state ->
        views?.render(state)
      }
    }
    viewModel.attachView(views!!.events().flowOn(Dispatchers.Main))
  }

  @CallSuper
  override fun onDestroyView() {
    views = null
    renderJob.cancel()
    viewModel.dropView()

    super.onDestroyView()
  }
}
