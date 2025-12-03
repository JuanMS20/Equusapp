package com.villalobos.caballoapp.ui.gamification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.data.model.BadgeEntity
import com.villalobos.caballoapp.databinding.ItemBadgeBinding

/**
 * Adapter para mostrar medallas en un RecyclerView.
 */
class BadgeAdapter : ListAdapter<BadgeEntity, BadgeAdapter.BadgeViewHolder>(BadgeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BadgeViewHolder(
        private val binding: ItemBadgeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(badge: BadgeEntity) {
            binding.apply {
                tvBadgeName.text = badge.name
                tvBadgeDescription.text = badge.description
                
                // Configurar icono
                ivBadgeIcon.setImageResource(badge.iconResId)
                
                // Configurar estado visual
                if (badge.isUnlocked) {
                    cardBadge.alpha = 1f
                    ivBadgeIcon.alpha = 1f
                    tvBadgeName.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.primary_brown)
                    )
                    ivLockIcon.visibility = android.view.View.GONE
                } else {
                    cardBadge.alpha = 0.6f
                    ivBadgeIcon.alpha = 0.4f
                    tvBadgeName.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.text_secondary)
                    )
                    ivLockIcon.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    private class BadgeDiffCallback : DiffUtil.ItemCallback<BadgeEntity>() {
        override fun areItemsTheSame(oldItem: BadgeEntity, newItem: BadgeEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BadgeEntity, newItem: BadgeEntity): Boolean {
            return oldItem == newItem
        }
    }
}
