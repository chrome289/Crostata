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

    var _id: String = ""
    var timeCreated = ""
    var contentId: String = ""
    var contentType: Int = 0
    var creatorId: String = ""
    var creatorName: String = ""
    var reporterId: String = ""
    var isReviewed: Boolean = false
    var isAccepted: Boolean = false

    //generated attributes
    lateinit var date: Date
    lateinit var timeCreatedText: String

    //set date
    private fun setDate() {
        date = inputFormat.parse(timeCreated)
    }

    //init extra attributes
    fun initExtraInfo() {
        setDate()
        setTimeCreatedText()
    }

    //use tiemago to create relative timespan strings
    private fun setTimeCreatedText() {
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = date

        timeCreatedText = TimeAgo.using(calendar.timeInMillis).capitalize()
    }

    //static stuff
    companion object {
        private val calendar = Calendar.getInstance()
        private val calendar2 = Calendar.getInstance()
        private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        const val MESSAGE_SUBMITTED: String = "Report submitted successfully"
        const val MESSAGE_NOT_SUBMITTED: String = "Report not submitted. Try again."

        //deep copy list
        fun cloneList(reportList: ArrayList<Report>): ArrayList<Report> {
            val newList = ArrayList<Report>()
            for (report in reportList) {
                newList.add(report.clone())
            }
            return newList
        }
    }
}