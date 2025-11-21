package com.example.newfrontcdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AssetsAdapter(
    private val dataList: List<AssetBean>,
    private val onItemClick: (AssetBean) -> Unit
) : RecyclerView.Adapter<AssetsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val ivCover: ImageView = view.findViewById(R.id.iv_cover)

        init {
            view.setOnClickListener {
                onItemClick(dataList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asset_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.tvName.text = item.name
        holder.tvDate.text = item.date
        holder.ivCover.setImageResource(item.coverResId)
    }

    override fun getItemCount() = dataList.size
}