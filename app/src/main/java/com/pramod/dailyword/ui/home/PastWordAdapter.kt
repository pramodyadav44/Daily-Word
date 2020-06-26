package com.pramod.dailyword.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pramod.dailyword.R
import com.pramod.dailyword.databinding.ItemPastWordLayoutBinding
import com.pramod.dailyword.db.model.WordOfTheDay

class PastWordAdapter(
    val onItemClickCallback: (Int, WordOfTheDay) -> Unit
) : ListAdapter<WordOfTheDay, PastWordAdapter.WordViewHolder>(diffCallback) {
    private var canStartActivity = true

    fun setCanStartActivity(canStart: Boolean) {
        canStartActivity = canStart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding: ItemPastWordLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_past_word_layout,
            parent,
            false
        )

        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.binding.wordOfTheDay = getItem(position)
        holder.binding.position = position
        holder.setUpListener(position)
        holder.binding.executePendingBindings()
    }


    inner class WordViewHolder(val binding: ItemPastWordLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setUpListener(position: Int) {
            binding.root.setOnClickListener {
                if (canStartActivity) {
                    canStartActivity = false;
                    onItemClickCallback.invoke(
                        position,
                        getItem(position)
                    )
                }
            }
        }
    }


    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<WordOfTheDay>() {
                override fun areItemsTheSame(
                    oldItem: WordOfTheDay,
                    newItem: WordOfTheDay
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: WordOfTheDay,
                    newItem: WordOfTheDay
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }

}

