package com.trialbot.trainyapplication.presentation.screen.message.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.databinding.ItemProgressBinding

class MessageLoadStateAdapter : LoadStateAdapter<MessageLoadStateAdapter.ItemViewHolder>() {
    override fun onBindViewHolder(holder: ItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ItemViewHolder {
        return when (loadState) {
            is LoadState.Loading, is LoadState.Error -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemProgressBinding.inflate(inflater, parent, false)
                ProgressViewHolder(binding)
            }
            is LoadState.NotLoading -> error("Not supported")
        }
    }

    abstract class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(loadState: LoadState)
    }

    class ProgressViewHolder(
        binding: ItemProgressBinding
    ) : ItemViewHolder(binding.root) {
        override fun bind(loadState: LoadState) {
            // nothing to do
        }
    }
}