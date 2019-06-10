package de.hannesstruss.alter.features.list

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.domain.PrettyAge
import java.time.OffsetDateTime

class BabyListItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
  var clickHandler: ((Int) -> Unit)? = null

  companion object {
    fun create(inflater: LayoutInflater, parent: ViewGroup): BabyListItemViewHolder {
      val view = inflater.inflate(R.layout.baby_list_item, parent, false)
      return BabyListItemViewHolder(view)
    }
  }

  init {
    view.setOnClickListener {
      val pos = adapterPosition
      if (pos != RecyclerView.NO_POSITION) {
        clickHandler?.invoke(pos)
      }
    }
  }

  fun bind(baby: Baby) {
    view.findViewById<TextView>(R.id.txt_name).text = baby.name
    view.findViewById<TextView>(R.id.txt_parents).text = baby.parents

    val age = baby.born_at?.let { bornAt ->
      val p = PrettyAge.of(bornAt, OffsetDateTime.now())
      val res = when (p) {
        is PrettyAge.Days -> R.plurals.list_age_fmt_days
        is PrettyAge.Weeks -> R.plurals.list_age_fmt_weeks
        is PrettyAge.Months -> R.plurals.list_age_fmt_months
        is PrettyAge.Years -> R.plurals.list_age_fmt_years
      }
      val text = SpannableString(view.context.resources.getQuantityString(res, p.significantValue.toInt(), p.significantValue))
      val numberSpan = TextAppearanceSpan(view.context, R.style.TextAppearance_MaterialComponents_Headline5)
      text.setSpan(numberSpan, 0, p.significantValue.toString().length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
      text
    } ?: ""
    view.findViewById<TextView>(R.id.txt_age).text = age
  }

  private fun Number.withUnit(unit: String): String {
    return when (this.toInt()) {
      0 -> ""
      else -> "$this$unit"
    }
  }
}
