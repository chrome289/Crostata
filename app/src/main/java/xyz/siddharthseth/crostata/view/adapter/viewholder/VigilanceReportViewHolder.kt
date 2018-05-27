package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_vigilance_report.view.*
import xyz.siddharthseth.crostata.data.model.retrofit.Report
import xyz.siddharthseth.crostata.util.recyclerView.VigilanceReportListener

class VigilanceReportViewHolder(itemView: View?, private val vigilanceReportListener: VigilanceReportListener) : RecyclerView.ViewHolder(itemView) {

    fun init(report: Report) {
        itemView.reportId.text = "Report #" + report._id
        itemView.timeText.text = report.timeCreatedText
        itemView.reportText.text = "Reported " +
                report.creatorName +
                " (ID:#" +
                report.creatorId +
                ") for " +
                when (report.contentType) {
                    0 -> "their Profile"
                    1 -> "an Inappropriate Post"
                    else -> "a Inappropriate Comment"
                }
        itemView.status.text =
                if (report.isAccepted) "ACCEPTED"
                else {
                    if (report.isReviewed)
                        "NOT ACCEPTED"
                    else
                        "PENDING REVIEW"
                }
        itemView.status.setTextColor(
                if (itemView.status.text == "ACCEPTED")
                    vigilanceReportListener.positiveColorTint
                else
                    vigilanceReportListener.negativeColorTint
        )
    }
}