package com.freezer.mathsolver.history

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freezer.mathsolver.R
import com.freezer.mathsolver.detailsolutionactivity.DetailSolutionActivity
import com.freezer.mathsolver.history.database.HistoryItemDatabase
import com.freezer.mathsolver.history.database.HistoryItemEntity
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HistoryFragment : Fragment(){
    val TAG = " HistoryFragment"

    lateinit var dataSet : ArrayList<HistoryItemEntity>
    lateinit var database: HistoryItemDatabase
    lateinit var historyItemAdapter: HistoryItemAdapter
    lateinit var linearLayoutManager : LinearLayoutManager

    lateinit var recyclerViewHistory : RecyclerView

    var isLoading = true
    var curPage =  0

    companion object {
        private val mInstance = HistoryFragment()

        @Synchronized
        fun getInstance(): HistoryFragment {
            return mInstance
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = HistoryItemDatabase.getDatabase(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewHistory = view.findViewById(R.id.rv_history)
        CoroutineScope(Dispatchers.IO).launch {
            populateDataSet(page = curPage)

            historyItemAdapter = HistoryItemAdapter(requireContext(), dataSet, listener)
            linearLayoutManager = LinearLayoutManager(requireContext())
            with (Dispatchers.Main){
                recyclerViewHistory.layoutManager = linearLayoutManager
                recyclerViewHistory.adapter = historyItemAdapter
                recyclerViewHistory.addOnScrollListener(scrollListener)
            }
        }
    }


    private val listener = object : HistoryItemListener {
        override fun onClick(historyItemEntity: HistoryItemEntity) {
            val detailSolutionActivityIntent = Intent(activity, DetailSolutionActivity::class.java)
            detailSolutionActivityIntent.putExtra("queryInputStr", historyItemEntity.item)
            detailSolutionActivityIntent.putExtra("isAdding", false)
            startActivity(detailSolutionActivityIntent)
        }

        override fun onLongClick(historyItemEntity: HistoryItemEntity) {
            val builder = AlertDialog.Builder(requireContext()).setMessage("Delete this item")
            builder.apply {
                setPositiveButton("OK",
                DialogInterface.OnClickListener{ dialog, id ->
                    CoroutineScope(Dispatchers.IO).launch {
                        database.historyItemDao().deleteHistoryItem(historyItemEntity)
                        activity?.runOnUiThread{
                            recyclerViewHistory.adapter?.notifyDataSetChanged()
                        }
                    }
                })
                setNegativeButton("Cancel",null)
            }
            builder.show()
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.childCount
            val totalItemCount = linearLayoutManager.itemCount
            val pastVisibleItems = linearLayoutManager.findLastCompletelyVisibleItemPosition()

            if(isLoading) {
                if(((visibleItemCount + pastVisibleItems) >= totalItemCount) || (totalItemCount <= pastVisibleItems)) {
                    isLoading = false
                    pb_history_wait.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDataSet(++curPage)
                        isLoading = true
                        activity?.runOnUiThread {
                            //rv_history.adapter?.notifyDataSetChanged()
                            pb_history_wait.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private suspend fun populateDataSet(page : Int) {
        if(page == 0) {
            dataSet = ArrayList(database.historyItemDao().getHistoryItems(page = 0))
        } else {
            dataSet.addAll(database.historyItemDao().getHistoryItems(page = page * 10))
        }
    }

    fun addLatestToDataSet() {
        CoroutineScope(Dispatchers.IO).launch {
            dataSet.add(0, database.historyItemDao().getLatestHistoryItem())
            recyclerViewHistory.adapter?.notifyItemInserted(0)
        }
    }
}