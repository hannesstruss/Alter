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
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class StateMachineFragment<StateT : Any, IntentT : Any, ViewModelT : StateMachineViewModel<StateT, IntentT>> : Fragment() {
  @get:LayoutRes abstract val layout: Int
  abstract val viewModelClass: Class<ViewModelT>
  abstract fun intents(): Observable<IntentT>
  abstract fun render(state: StateT)

  private lateinit var viewModel: ViewModelT
  private var stateDisposable: Disposable? = null

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
  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(layout, container, false)
  }

  @CallSuper
  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)

    stateDisposable = viewModel.states.subscribe { render(it) }
    viewModel.attachView(intents().share())
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()

    stateDisposable?.dispose()
    viewModel.dropView()
  }
}
