package com.pramod.dailyword.ui.word_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pramod.dailyword.R
import com.pramod.dailyword.databinding.ItemWordExampleLayoutBinding

class ExampleAdapter(
    private var examples: List<String>? = null,
    private var colorResId: Int? = null,
    private var desaturatedColorResId: Int? = null
) :
    RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>() {

    fun setExamples(examples: List<String>?, colorResId: Int?, desaturatedColorResId: Int?) {
        this.examples = examples
        this.colorResId = colorResId;
        this.desaturatedColorResId = desaturatedColorResId;
        notifyDataSetChanged()
    }

    class ExampleViewHolder(val binding: ItemWordExampleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val binding: ItemWordExampleLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_word_example_layout,
            parent,
            false
        )
        return ExampleViewHolder(binding)
    }

    override fun getItemCount(): Int = examples?.size ?: 0

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        holder.binding.colorResId = colorResId
        holder.binding.desaturatedColorResId = desaturatedColorResId
        holder.binding.srNo = position + 1
        holder.binding.example = examples?.get(position)
        holder.binding.executePendingBindings()
    }
}