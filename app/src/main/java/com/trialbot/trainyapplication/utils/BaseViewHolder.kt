package com.trialbot.trainyapplication.utils

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
    abstract fun bind(item: T, resources: Resources)
}