package xyz.siddharthseth.crostata.data.model

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Post : Comparable<Post> {
    override fun compareTo(other: Post): Int {
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar2.timeZone = TimeZone.getTimeZone("UTC")

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

        calendar.time = inputFormat.parse(this.timeCreated)
        calendar2.time = inputFormat.parse(other.timeCreated)

        return (calendar2.timeInMillis - calendar.timeInMillis).toInt()
    }

    var postId = ""
    var creatorName = ""
    var timeCreated = ""
    var contentType = ""
    var text = ""
    var upVotes = 0
    var downVotes = 0
    var isCensored = false
}