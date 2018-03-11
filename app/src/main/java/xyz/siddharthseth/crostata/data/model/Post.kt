package xyz.siddharthseth.crostata.data.model

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Post : Comparable<Post> {
    override fun compareTo(other: Post): Int {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
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
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

        val calendar = Calendar.getInstance()
        calendar.time = inputFormat.parse(this.timeCreated)
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        return calendar.timeInMillis / 1000.0f
    }

    var postId = ""
    var creatorName = ""
    var creatorId = ""
    var timeCreated = ""
    var contentType = ""
    var text = ""
    var votes = 0
    var isCensored = false
    var opinion = 0
}