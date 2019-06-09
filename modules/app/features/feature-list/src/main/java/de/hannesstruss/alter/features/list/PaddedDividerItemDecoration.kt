package de.hannesstruss.alter.features.list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class PaddedDividerItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {
  companion object {
    private val ATTRS = intArrayOf(android.R.attr.listDivider)
    private const val PADDING_LEFT_DP = 72
  }

  private var divider: Drawable? = null
  private val mBounds = Rect()
  private val paddingLeftPx: Int

  /**
   * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
   * [LinearLayoutManager].
   *
   * @param context Current context, it will be used to access resources.
   * @param orientation Divider orientation. Should be [.HORIZONTAL] or [.VERTICAL].
   */
  init {
    val a = context.obtainStyledAttributes(ATTRS)
    divider = a.getDrawable(0)
    if (divider == null) {
      Timber.w(
        "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()"
      )
    }
    a.recycle()
    paddingLeftPx = (context.resources.displayMetrics.density * PADDING_LEFT_DP).toInt()
  }

  /**
   * Sets the [Drawable] for this divider.
   *
   * @param drawable Drawable that should be used as a divider.
   */
  fun setDrawable(drawable: Drawable) {
    divider = drawable
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    if (parent.layoutManager == null || divider == null) {
      return
    }
    drawVertical(c, parent)
  }

  private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
    canvas.save()
    val left: Int
    val right: Int

    if (parent.clipToPadding) {
      left = parent.paddingLeft + paddingLeftPx
      right = parent.width - parent.paddingRight
      canvas.clipRect(
        left, parent.paddingTop, right,
        parent.height - parent.paddingBottom
      )
    } else {
      left = 0
      right = parent.width
    }

    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      parent.getDecoratedBoundsWithMargins(child, mBounds)
      val bottom = mBounds.bottom + Math.round(child.translationY)
      val top = bottom - divider!!.intrinsicHeight
      divider!!.setBounds(left, top, right, bottom)
      divider!!.draw(canvas)
    }
    canvas.restore()
  }

  override fun getItemOffsets(
    outRect: Rect, view: View, parent: RecyclerView,
    state: RecyclerView.State
  ) {
    if (divider == null) {
      outRect.set(0, 0, 0, 0)
      return
    }
    outRect.set(0, 0, 0, divider!!.intrinsicHeight)
  }
}
