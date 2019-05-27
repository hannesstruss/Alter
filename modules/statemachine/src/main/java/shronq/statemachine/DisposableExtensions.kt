package shronq.statemachine

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

internal operator fun CompositeDisposable.plusAssign(other: Disposable) {
  add(other)
}
