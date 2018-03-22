package xyz.siddharthseth.crostata.data.model

import java.text.SimpleDateFormat
import java.util.*

class Comment : Comparable<Comment> {
    override fun compareTo(other: Comment): Int {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar2.timeZone = TimeZone.getTimeZone("UTC")

        calendar.time = inputFormat.parse(this.timeCreated)
        calendar2.time = inputFormat.parse(other.timeCreated)

        return when {
            (calendar2.timeInMillis - calendar.timeInMillis) > 0 -> 1
            (calendar2.timeInMillis - calendar.timeInMillis) < 0 -> -1
            else -> 0
        }
    }

    fun getTimestamp(): Float {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

        val calendar = Calendar.getInstance()
        calendar.time = inputFormat.parse(this.timeCreated)
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        return calendar.timeInMillis / 1000.0f
    }

    var _id: String = ""
    var name: String = ""
    var birthId = ""
    var text: String = ""
    var timeCreated = ""
}
