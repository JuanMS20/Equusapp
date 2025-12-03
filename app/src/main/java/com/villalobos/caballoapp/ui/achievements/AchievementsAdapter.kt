package com.villalobos.caballoapp.ui.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.databinding.ItemAchievementBinding

class AchievementsAdapter : ListAdapter<AchievementsViewModel.AchievementItem, AchievementsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AchievementsViewModel.AchievementItem) {
            val context = binding.root.context
            val achievement = item.achievement

            binding.tvTitle.text = achievement.title
            binding.tvDescription.text = achievement.description
            binding.tvPoints.text = "${achievement.points} XP"

            if (item.isUnlocked) {
                binding.imgIcon.setImageResource(achievement.iconRes)
                binding.imgIcon.alpha = 1.0f
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface_variant))
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.primary_brown))
            } else {
                binding.imgIcon.setImageResource(R.drawable.ic_lock) // Asegúrate de tener un icono de candado o usa uno genérico
                binding.imgIcon.alpha = 0.5f
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.background))
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AchievementsViewModel.AchievementItem>() {
        override fun areItemsTheSame(oldItem: AchievementsViewModel.AchievementItem, newItem: AchievementsViewModel.AchievementItem): Boolean {
            return oldItem.achievement.id == newItem.achievement.id
        }

        override fun areContentsTheSame(oldItem: AchievementsViewModel.AchievementItem, newItem: AchievementsViewModel.AchievementItem): Boolean {
            return oldItem == newItem
        }
    }
}
