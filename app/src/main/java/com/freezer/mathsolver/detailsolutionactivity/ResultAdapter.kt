package com.freezer.mathsolver.detailsolutionactivity

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.freezer.mathsolver.R
import com.freezer.mathsolver.wolframalpha.PodsItem

class ResultAdapter(val context : Context, val dataset: List<PodsItem?>?) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    class ResultViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imageLayoutContainer = itemView.findViewById<LinearLayout>(R.id.linear_layout_detail_solution)
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_title_solution)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_solution, parent, false)
        return ResultViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(dataset == null)
            return 0
        return dataset.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = dataset!![position]
        holder.tvTitle.text = result?.title

        val subPods = result?.subPods

        subPods?.forEach {
            val imgResult = ImageView(context)
            holder.imageLayoutContainer.addView(imgResult)
            Glide.with(context)
                .asBitmap()
                .load(it?.img?.src)
                .placeholder(R.drawable.ic_baseline_cloud_download_24)
                .error(R.drawable.ic_baseline_cloud_off_24)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val resizedResource = Bitmap.createScaledBitmap(resource, resource.width * 4, resource.height * 4, true)

                        imgResult.setImageBitmap(resizedResource)
                    }
                })
            holder.imageLayoutContainer.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.setIsRecyclable(false)
        }
    }
}