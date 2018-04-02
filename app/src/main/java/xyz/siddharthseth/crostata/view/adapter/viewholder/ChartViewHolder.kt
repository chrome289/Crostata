package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_patriot_chart.view.*
import xyz.siddharthseth.crostata.data.model.retrofit.ChartEntry
import xyz.siddharthseth.crostata.viewmodel.fragment.CommunityViewModel

class ChartViewHolder(itemView: View, communityViewModel: CommunityViewModel) : RecyclerView.ViewHolder(itemView) {

    private var listener = communityViewModel
    val TAG: String = javaClass.simpleName

    fun init(chartEntry: ChartEntry, position: Int) {
        itemView.rankOwn.text = (position + 1).toString()
        itemView.profileName.text = chartEntry.name
        itemView.patriotIndex.text = chartEntry.patriotIndex.toString()

        listener.loadProfileImage(chartEntry.birthId, itemView.profileImage, 128)

    }
}