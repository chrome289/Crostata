package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.Report
import xyz.siddharthseth.crostata.util.recyclerView.VigilanceReportListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.VigilanceReportViewHolder

/**
 *adapter for vigilance report list
 */
class VigilanceReportAdapter(private val vigilanceReportListener: VigilanceReportListener) : RecyclerView.Adapter<VigilanceReportViewHolder>() {

    var reportList = ArrayList<Report>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VigilanceReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_vigilance_report, parent, false)
        return VigilanceReportViewHolder(view, vigilanceReportListener)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: VigilanceReportViewHolder, position: Int) {
        holder.init(reportList[position])
    }
}