package com.freezer.mathsolver.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freezer.mathsolver.R
import com.freezer.mathsolver.history.database.HistoryItemEntity
import io.github.kexanie.library.MathView

class HistoryItemAdapter(val context: Context, val dataSet : List<HistoryItemEntity>? , val listener: HistoryItemListener) :
    RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>(){
    class HistoryItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val mvHistoryItem = itemView.findViewById<MathView>(R.id.mv_history_item)
        val tvDateTime = itemView.findViewById<TextView>(R.id.tv_history_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return HistoryItemAdapter.HistoryItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(dataSet == null)
            return 0
        return dataSet.size
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        val item = dataSet!![position]
        holder.mvHistoryItem.text = "$$" + item.item + "$$"
        holder.tvDateTime.text = item.timeStamp
        holder.itemView.setOnClickListener {
            listener.onClick(item)
        }
        holder.itemView.setOnLongClickListener {
            listener.onLongClick(item)
            true
        }
    }
}