package de.hannesstruss.alter.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class ActivityComponentHolder : Fragment(), ActivityFinder {
  private lateinit var component: ActivityComponent

  companion object {
    private const val TAG = "ActivityComponentHolder"

    fun get(activity: FragmentActivity): ActivityComponent {
      val fm = activity.supportFragmentManager

      val holder = fm.findFragmentByTag(TAG) as? ActivityComponentHolder
      if (holder == null) {
        val newHolder = ActivityComponentHolder()
        fm.beginTransaction().add(newHolder, TAG).commitNow()
        return newHolder.component
      } else {
        return holder.component
      }
    }
  }

  init {
    retainInstance = true
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    component = DaggerActivityComponent
      .factory()
      .create(
        activityFinder = this,
        appComponent = AppComponent.from(context)
      )
  }

  override fun findActivity() = requireActivity()
}
