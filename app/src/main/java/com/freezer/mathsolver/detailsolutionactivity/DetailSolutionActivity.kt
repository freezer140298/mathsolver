package com.freezer.mathsolver.detailsolutionactivity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.freezer.mathsolver.R
import com.freezer.mathsolver.history.HistoryFragment
import com.freezer.mathsolver.history.database.HistoryItemDatabase
import com.freezer.mathsolver.wolframalpha.ResponseResult
import com.freezer.mathsolver.wolframalpha.WolframAlphaService
import kotlinx.android.synthetic.main.activity_detail_solution.*
import kotlinx.coroutines.*
import java.text.DateFormat
import java.util.*

class DetailSolutionActivity : AppCompatActivity() {
    companion object {
        val TAG = "DetailSolutionActivity"
    }

    lateinit var resultAdapter : ResultAdapter
    var queryInputStr : String? = null
    var isAdding: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_solution)

        pb_wait.visibility = View.VISIBLE

        setTitle("Result")
        queryInputStr = intent.extras?.getString("queryInputStr")
        isAdding = intent.extras?.getBoolean("isAdding")

        var responseResult : ResponseResult? = null

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            responseResult = WolframAlphaService.getInstance().getApi().query(queryInputStr!!)
            val podList = responseResult?.queryResult?.pods
            resultAdapter = ResultAdapter(this@DetailSolutionActivity, podList)

            if(podList == null) {
                runOnUiThread{
                    tv_no_result.visibility = View.VISIBLE
                }
            }
            else if(isAdding!!){
                HistoryItemDatabase.getDatabase(this@DetailSolutionActivity).historyItemDao().insertHistoryItem(queryInputStr!!,
                                                                                                                        DateFormat.getDateTimeInstance().format(Calendar.getInstance().time))
                HistoryFragment.getInstance().addLatestToDataSet()
            }
            runOnUiThread {
                Log.d(TAG, podList.toString())
                rv_detail_solution.layoutManager = LinearLayoutManager(this@DetailSolutionActivity)
                rv_detail_solution.adapter = resultAdapter
                pb_wait.visibility = View.GONE
                rv_detail_solution.visibility = View.VISIBLE
            }
        }
    }
}