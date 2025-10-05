package com.example.enumerator_monitor.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.enumerator_monitor.data.SurveyEntry
import com.example.enumerator_monitor.databinding.ItemRecentEntryBinding
import com.example.enumerator_monitor.viewmodel.DayGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupedEntriesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<Item>()
    var onItemClick: (SurveyEntry) -> Unit = {}
    var onShareClick: (SurveyEntry) -> Unit = {}

    fun submit(groups: List<DayGroup>) {
        items.clear()
        groups.forEach { g ->
            items.add(Item.Header(g.dateLabel))
            g.entries.forEach { entry -> items.add(Item.Row(entry)) }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Item.Header -> TYPE_HEADER
        is Item.Row -> TYPE_ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) HeaderVH(inflater, parent) else RowVH(inflater, parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Item.Header -> (holder as HeaderVH).bind(item.title)
            is Item.Row -> (holder as RowVH).bind(item, onItemClick, onShareClick)
        }
    }

    private sealed class Item {
        data class Header(val title: String) : Item()
        data class Row(val entry: SurveyEntry) : Item()
    }

    private class HeaderVH(private val textView: android.widget.TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(title: String) { textView.text = title }
        companion object {
            operator fun invoke(inflater: LayoutInflater, parent: ViewGroup): HeaderVH {
                val context = parent.context
                val tv = android.widget.TextView(context).apply {
                    setTextAppearance(context, com.example.enumerator_monitor.R.style.textHeading)
                    setPadding(0, 24, 0, 12)
                }
                return HeaderVH(tv)
            }
        }
    }

    private class RowVH private constructor(private val binding: ItemRecentEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item.Row, onItemClick: (SurveyEntry) -> Unit, onShareClick: (SurveyEntry) -> Unit) {
            val entry = item.entry
            binding.tvPersonName.text = entry.respondentName
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(entry.createdAt))
            binding.tvMeta.text = "House # ${entry.houseNo}     $time"

            binding.root.setOnClickListener { onItemClick(entry) }
            binding.btnShare.setOnClickListener { onShareClick(entry) }
        }

        companion object {
            operator fun invoke(inflater: LayoutInflater, parent: ViewGroup): RowVH {
                val binding = ItemRecentEntryBinding.inflate(inflater, parent, false)
                return RowVH(binding)
            }
        }
    }

    private companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ROW = 1
    }
}


