package com.freezer.mathsolver.history

import com.freezer.mathsolver.history.database.HistoryItemEntity

interface HistoryItemListener {
    fun onClick(historyItemEntity: HistoryItemEntity)

    fun onLongClick(historyItemEntity: HistoryItemEntity)
}