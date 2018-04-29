package xyz.siddharthseth.crostata.data.model

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.github.marlonlom.utilities.timeago.TimeAgo
import java.text.SimpleDateFormat
import java.util.*

open class Comment : Comparable<Comment> {
    override fun compareTo(other: Comment): Int {
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

    var _id: String = ""
    var name: String = ""
    var postId = ""
    var birthId = ""
    var text: String = ""
    var timeCreated = ""
    var isGenerated = false
    var isCensored = false

    lateinit var glideUrlProfileThumb: GlideUrl
    lateinit var timeCreatedText: String
    lateinit var date: Date


    fun getTimestamp(): Long {
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = date

        return calendar.timeInMillis
    }

    private fun setDate() {
        date = inputFormat.parse(timeCreated)
    }

    private fun setTimeCreatedText() {
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = date

        timeCreatedText = TimeAgo.using(calendar.timeInMillis).capitalize()
    }

    private fun setGlideUrlProfileThumb(baseUrl: String, dimen: Int, quality: Int, token: String) {
        glideUrlProfileThumb = GlideUrl(baseUrl +
                "subject/profileImage?birthId=$birthId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())
    }

    fun initExtraInfo(baseUrl: String, token: String) {
        val dimen2 = 64
        val quality = 80
        setDate()
        setTimeCreatedText()
        setGlideUrlProfileThumb(baseUrl, dimen2, quality, token)
    }

    companion object {
        private val calendar = Calendar.getInstance()
        private val calendar2 = Calendar.getInstance()
        private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    }
}