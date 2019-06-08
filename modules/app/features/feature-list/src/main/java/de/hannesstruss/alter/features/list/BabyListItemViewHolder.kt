package de.hannesstruss.alter.features.list

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

    val age = baby.born_at?.let { bornAt ->
      val p = PrettyAge.of(bornAt, OffsetDateTime.now())
      when (p) {
        is PrettyAge.Days -> "${p.days}d"
        is PrettyAge.Weeks -> "${p.weeks}w ${p.days}d"
        is PrettyAge.Months -> "${p.months}m ${p.weeks}w"
        is PrettyAge.Years -> "${p.years}y ${p.months}m"
      }
    } ?: ""
    view.findViewById<TextView>(R.id.txt_age).text = age
  }
}
