package de.hannesstruss.alter.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hannesstruss.alter.db.Baby
import de.hannesstruss.alter.domain.LessSignificantValueMode
import de.hannesstruss.alter.domain.PrettyAge
import de.hannesstruss.alter.domain.format
import de.hannesstruss.alter.features.list.databinding.BabyListItemBinding
import java.time.LocalDate

class BabyListItemViewHolder(private val binding: BabyListItemBinding) : RecyclerView.ViewHolder(binding.root) {
  var clickHandler: ((Int) -> Unit)? = null

  companion object {
    fun create(inflater: LayoutInflater, parent: ViewGroup): BabyListItemViewHolder {
      val binding = BabyListItemBinding.inflate(inflater, parent, false)
      return BabyListItemViewHolder(binding)
    }
  }

  init {
    binding.root.setOnClickListener {
      val pos = adapterPosition
      if (pos != RecyclerView.NO_POSITION) {
        clickHandler?.invoke(pos)
      }
    }
  }

  fun bind(baby: Baby) {
    binding.txtName.text = baby.name
    binding.txtParents.text = baby.parents

    val age = baby.born_at?.let { bornAt ->
      PrettyAge.of(bornAt, LocalDate.now()).format(
        context = binding.root.context,
        lessSignificantValueMode = LessSignificantValueMode.Never,
        numberTextAppearance = R.style.TextAppearance_MaterialComponents_Headline5
      )
    } ?: ""
    binding.txtAge.text = age
  }
}
