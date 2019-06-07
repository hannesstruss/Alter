package de.hannesstruss.alter.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.alter.db.Baby
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow

class BabyListAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<BabyListItemViewHolder>() {
  private val items = mutableListOf<Baby>()
  private val _clickedIds = BroadcastChannel<Long>(16)
  val clickedIds = _clickedIds.asFlow()

  override fun getItemCount() = items.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BabyListItemViewHolder {
    val vh = BabyListItemViewHolder.create(inflater, parent)
    vh.clickHandler = { position ->
      val item = items[position]
      _clickedIds.offer(item.id)
    }
    return vh
  }

  override fun onBindViewHolder(holder: BabyListItemViewHolder, position: Int) {
    holder.bind(items[position])
  }

  fun setBabies(babies: List<Baby>) {
    items.clear()
    items.addAll(babies)
    notifyDataSetChanged()
  }
}
