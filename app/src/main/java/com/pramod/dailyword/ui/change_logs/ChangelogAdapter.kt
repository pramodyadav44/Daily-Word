package com.pramod.dailyword.ui.change_logs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBinderMapperImpl
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pramod.dailyword.R
import com.pramod.dailyword.databinding.ItemChangeLogLayoutBinding
import com.pramod.dailyword.db.model.Changes


class ChangelogAdapter(private val changesList: List<Changes>?) :
    RecyclerView.Adapter<ChangelogAdapter.ChangelogViewHolder>() {


    class ChangelogViewHolder(val binding: ItemChangeLogLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangelogViewHolder {
        val binding: ItemChangeLogLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_change_log_layout,
            parent,
            false
        )
        return ChangelogViewHolder(binding)
    }

    override fun getItemCount() = changesList?.size ?: 0

    override fun onBindViewHolder(holder: ChangelogViewHolder, position: Int) {
        holder.binding.changes = changesList?.get(position)
        holder.binding.executePendingBindings()
    }

}