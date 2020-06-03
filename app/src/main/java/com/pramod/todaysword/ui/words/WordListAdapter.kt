package com.pramod.todaysword.ui.words

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pramod.todaysword.R
import com.pramod.todaysword.databinding.ItemNetworkStateLayoutBinding
import com.pramod.todaysword.databinding.ItemWordListLayoutBinding
import com.pramod.todaysword.db.model.NetworkState
import com.pramod.todaysword.db.model.NetworkState.Companion.LOADED
import com.pramod.todaysword.db.model.Status
import com.pramod.todaysword.db.model.WordOfTheDay
import java.lang.IllegalArgumentException

class WordListAdapter(
    val itemClickCallback: ((pos: Int, word: WordOfTheDay) -> Unit)? = null,
    val retryCallback: () -> Unit
) :
    PagedListAdapter<WordOfTheDay, RecyclerView.ViewHolder>(diffCallback) {
    private var networkState: NetworkState? = null

    inner class WordViewHolder(private val binding: ItemWordListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, word: WordOfTheDay?) {
            binding.wordOfTheDay = word
            binding.itemWordListCardView.setOnClickListener {
                itemClickCallback?.invoke(position, word!!)
            }
            binding.executePendingBindings()
        }
    }

    class NetworkStateViewHolder(
        private val binding: ItemNetworkStateLayoutBinding,
        private val retryCallback: () -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.networkStateBtnRetry.setOnClickListener {
                retryCallback()
            }
        }

        fun bindTo(networkState: NetworkState?) {
            binding.networkStateProgress.visibility =
                if (networkState?.status == Status.RUNNING) View.VISIBLE else View.INVISIBLE
            binding.networkStateErrorLayout.visibility =
                if (networkState?.status == Status.FAILED) View.VISIBLE else View.INVISIBLE
            binding.networkStateBtnRetry.visibility =
                if (networkState?.status == Status.FAILED) View.VISIBLE else View.INVISIBLE
            binding.networkStateTxtViewError.visibility =
                if (networkState?.msg != null) View.VISIBLE else View.INVISIBLE
            binding.networkStateTxtViewError.text =
                networkState?.msg
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_word_list_layout -> WordViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_word_list_layout,
                    parent,
                    false
                )
            )
            R.layout.item_network_state_layout -> NetworkStateViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_network_state_layout,
                    parent,
                    false
                ), retryCallback
            )
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_network_state_layout ->
                (holder as NetworkStateViewHolder).bindTo(networkState)
            R.layout.item_word_list_layout ->
                (holder as WordViewHolder).bind(position, getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_network_state_layout
        } else {
            R.layout.item_word_list_layout
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + (if (hasExtraRow()) 1 else 0)
    }

    private fun hasExtraRow() = networkState != null && networkState != LOADED

    fun setNetworkState(networkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = networkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow()) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != networkState) {
            notifyItemChanged(itemCount - 1)
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