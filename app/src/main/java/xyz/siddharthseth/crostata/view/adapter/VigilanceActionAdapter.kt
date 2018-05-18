package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.VigilanceAction
import xyz.siddharthseth.crostata.util.recyclerView.VigilanceActionListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.VigilanceActionViewHolder

class VigilanceActionAdapter(var vigilanceActionListener: VigilanceActionListener) : RecyclerView.Adapter<VigilanceActionViewHolder>() {

    var actionReportList = ArrayList<VigilanceAction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VigilanceActionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_vigilance_action, parent, false)
        return VigilanceActionViewHolder(view, vigilanceActionListener)
    }

    override fun getItemCount(): Int {
        return actionReportList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: VigilanceActionViewHolder, position: Int) {
        holder.init(actionReportList[position])
    }

}