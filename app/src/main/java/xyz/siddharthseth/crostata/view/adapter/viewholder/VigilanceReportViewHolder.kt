package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_vigilance_report.view.*
import xyz.siddharthseth.crostata.data.model.retrofit.Report
import xyz.siddharthseth.crostata.data.repository.ContentRepository
import xyz.siddharthseth.crostata.util.recyclerView.VigilanceReportListener

/**
 * viewholder for vigilance report
 */
class VigilanceReportViewHolder(itemView: View?, private val vigilanceReportListener: VigilanceReportListener) : RecyclerView.ViewHolder(itemView) {

    /**
     * init vigilance report
     */
    fun init(report: Report) {
        itemView.reportId.text = "Report #${report._id}"
        itemView.timeText.text = report.timeCreatedText
        itemView.reportText.text = "Reported ${report.creatorName} (ID:#${report.creatorId}) for " +
                when (report.contentType) {
                    ContentRepository.PROFILE -> "their Profile"
                    ContentRepository.POST -> "an Inappropriate Post"
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