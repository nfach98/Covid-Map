package com.nfach98.covidmap.ui.main.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nfach98.covidmap.databinding.ItemsHistoryBinding
import com.nfach98.covidmap.model.History

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var histories = ArrayList<History>()
        set(histories) {
            if (histories.size > 0) {
                this.histories.clear()
            }
            this.histories.addAll(histories)
            notifyDataSetChanged()
        }

    private var onItemActionCallback: OnItemActionCallback? = null

    fun setOnItemClickCallback(onItemActionCallback: OnItemActionCallback) {
        this.onItemActionCallback = onItemActionCallback
    }

    fun addItem(history: History) {
        this.histories.add(history)
        notifyItemInserted(this.histories.size - 1)
    }

    fun updateItem(position: Int, history: History) {
        this.histories[position] = history
        notifyItemChanged(position, history)
    }

    fun removeItem(position: Int) {
        this.histories.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.histories.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = ItemsHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(histories[position])
    }

    override fun getItemCount(): Int = histories.size

    inner class HistoryViewHolder(private val binding: ItemsHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(history: History){
            with(binding){
                tvFrom.text = history.from
                tvTo.text = history.to
                tvTime.text = history.date
            }
        }
    }

    interface OnItemActionCallback {
        fun onItemClicked(data: History)
    }
}