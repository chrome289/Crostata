package xyz.siddharthseth.crostata.data.model.retrofit

import com.github.marlonlom.utilities.timeago.TimeAgo
import java.text.SimpleDateFormat
import java.util.*

class Report : Comparable<Report>, Cloneable {
    override fun compareTo(other: Report): Int {
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar2.timeZone = TimeZone.getTimeZone("UTC")

        calendar.time = date
        calendar2.time = other.date

        return when {
            (calendar2.timeInMillis - calendar.timeInMillis) > 0 -> 1
            (calendar2.timeInMillis - calendar.timeInMillis) < 0 -> -1
            else -> 0
        }
    }

    override fun clone(): Report {
        return super.clone() as Report
    }

    lateinit var _id: String
    var timeCreated = ""
    lateinit var contentId: String
    var contentType: Int = 0
    lateinit var creatorId: String
    lateinit var reporterId: String
    var isReviewed: Boolean = false
    var isAccepted: Boolean = false
    lateinit var creatorName: String

    lateinit var date: Date
    lateinit var timeCreatedText: String

    private fun setDate() {
        date = inputFormat.parse(timeCreated)
    }

    fun initExtraInfo() {
        setDate()
        setTimeCreatedText()
    }

    private fun setTimeCreatedText() {
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = date

        timeCreatedText = TimeAgo.using(calendar.timeInMillis).capitalize()
    }

    companion object {
        private val calendar = Calendar.getInstance()
        private val calendar2 = Calendar.getInstance()
        private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        fun cloneList(reportList: ArrayList<Report>): ArrayList<Report> {
            val newList = ArrayList<Report>()
            for (report in reportList) {
                newList.add(report.clone())
            }
            return newList
        }
    }
}