package xyz.siddharthseth.crostata.data.model.retrofit

import android.os.Parcel
import android.os.Parcelable
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.github.marlonlom.utilities.timeago.TimeAgo
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Post() : Comparable<Post>, Parcelable {
    class PostGlove {
        var requestId: String = ""
        var list: List<Post> = ArrayList<Post>()
    }

    override fun equals(other: Any?): Boolean {
        other as Post
        if (_id != other._id)
            return false
        if (opinion != other.opinion)
            return false
        if (comments != other.comments)
            return false
        if (this.votes != other.votes)
            return false
        return true
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(creatorName)
        parcel.writeString(creatorId)
        parcel.writeString(timeCreated)
        parcel.writeString(contentType)
        parcel.writeString(text)
        parcel.writeInt(votes)
        parcel.writeInt(comments)
        parcel.writeByte(if (isCensored) 1 else 0)
        parcel.writeByte(if (isGenerated) 1 else 0)
        parcel.writeInt(opinion)
        parcel.writeString(imageId)

        parcel.writeString(timeCreatedText)
        parcel.writeSerializable(date)
    }

    override fun compareTo(other: Post): Int {
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

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + creatorName.hashCode()
        result = 31 * result + creatorId.hashCode()
        result = 31 * result + timeCreated.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + votes
        result = 31 * result + comments
        result = 31 * result + isCensored.hashCode()
        result = 31 * result + isGenerated.hashCode()
        result = 31 * result + opinion
        result = 31 * result + imageId.hashCode()

        result = 31 * result + timeCreatedText.hashCode()

        return result
    }

    var _id: String = ""
    var creatorName = ""
    var creatorId = ""
    var timeCreated = ""
    var contentType = ""
    var text = ""
    var votes = Int.MAX_VALUE
    var comments = Int.MAX_VALUE
    var isCensored = false
    var isGenerated = false
    var opinion = Int.MAX_VALUE
    var imageId = ""

    lateinit var timeCreatedText: String
    lateinit var date: Date
    lateinit var glideUrl: GlideUrl
    lateinit var glideUrlThumb: GlideUrl
    lateinit var glideUrlProfileThumb: GlideUrl


    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        creatorName = parcel.readString()
        creatorId = parcel.readString()
        timeCreated = parcel.readString()
        contentType = parcel.readString()
        text = parcel.readString()
        votes = parcel.readInt()
        comments = parcel.readInt()
        isCensored = parcel.readByte() != 0.toByte()
        isGenerated = parcel.readByte() != 0.toByte()

        opinion = parcel.readInt()
        imageId = parcel.readString()

        timeCreatedText = parcel.readString()
        date = parcel.readSerializable() as Date
    }

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

        timeCreatedText = TimeAgo.using(calendar.timeInMillis)
    }

    private fun setGlideUrl(baseUrl: String, dimen: Int, quality: Int, token: String) {
        glideUrl = GlideUrl(baseUrl +
                "content/postedImage?imageId=$imageId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())
    }

    private fun setGlideUrlThumb(baseUrl: String, dimen: Int, quality: Int, token: String) {
        glideUrlThumb = GlideUrl(baseUrl +
                "content/postedImage?imageId=$imageId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())
    }

    private fun setGlideUrlProfileThumb(baseUrl: String, dimen: Int, quality: Int, token: String) {
        glideUrlProfileThumb = GlideUrl(baseUrl +
                "subject/profileImage?birthId=$creatorId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())
    }

    fun initExtraInfo(baseUrl: String, token: String) {
        setDate()
        setTimeCreatedText()
        setGlideUrls(baseUrl, token)
    }

    private fun setGlideUrls(baseUrl: String, token: String) {
        val dimen = 640
        val dimen2 = 64
        val dimen3 = 8
        val quality = 80
        setGlideUrl(baseUrl, dimen, quality, token)
        setGlideUrlThumb(baseUrl, dimen3, quality, token)
        setGlideUrlProfileThumb(baseUrl, dimen2, quality, token)
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        private val calendar = Calendar.getInstance()
        private val calendar2 = Calendar.getInstance()
        private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}