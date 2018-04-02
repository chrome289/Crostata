package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.ChartEntry
import xyz.siddharthseth.crostata.view.adapter.viewholder.ChartViewHolder
import xyz.siddharthseth.crostata.viewmodel.fragment.CommunityViewModel

class ChartAdapter(val communityViewModel: CommunityViewModel): RecyclerView.Adapter<ChartViewHolder>() {

    var chartEntries = ArrayList<ChartEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_patriot_chart, parent, false)
        return ChartViewHolder(view, communityViewModel)
    }

    override fun getItemCount(): Int {
        return chartEntries.size
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        holder.init(chartEntries[position], position)
    }

    override fun getItemId(position: Int): Long {
        return chartEntries[position].birthId.toLong()
    }
}