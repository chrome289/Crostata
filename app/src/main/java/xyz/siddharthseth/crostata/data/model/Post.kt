package xyz.siddharthseth.crostata.data.model

import android.os.Parcel
import android.os.Parcelable
import com.github.marlonlom.utilities.timeago.TimeAgo
import java.text.SimpleDateFormat
import java.util.*

class Post() : Comparable<Post>, Parcelable {

    override fun equals(other: Any?): Boolean {
        other as Post
        if (_id != other._id)
            return false
        if (opinion != other.opinion)
            return false
        if (comments != other.comments)
            return false
        if (downVotes != other.downVotes)
            return false
        if (upVotes != other.upVotes)
            return false
        if (votes != other.votes)
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
        parcel.writeInt(upVotes)
        parcel.writeInt(downVotes)
        parcel.writeInt(comments)
        parcel.writeByte(if (isCensored) 1 else 0)
        parcel.writeInt(opinion)
        parcel.writeString(imageId)

        parcel.writeInt(votes)
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
        result = 31 * result + upVotes
        result = 31 * result + downVotes
        result = 31 * result + comments
        result = 31 * result + isCensored.hashCode()
        result = 31 * result + opinion
        result = 31 * result + imageId.hashCode()

        result = 31 * result + votes
        result = 31 * result + timeCreatedText.hashCode()

        return result
    }


    var _id: String = ""
    var creatorName = ""
    var creatorId = ""
    var timeCreated = ""
    var contentType = ""
    var text = ""
    var upVotes = 0
    var downVotes = 0
    var comments = 0
    var isCensored = false
    var opinion = 0
    var imageId = ""

    var votes = 0
    var timeCreatedText: String = ""
    var date: Date = Date()


    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        creatorName = parcel.readString()
        creatorId = parcel.readString()
        timeCreated = parcel.readString()
        contentType = parcel.readString()
        text = parcel.readString()
        upVotes = parcel.readInt()
        downVotes = parcel.readInt()
        comments = parcel.readInt()
        isCensored = parcel.readByte() != 0.toByte()
        opinion = parcel.readInt()
        imageId = parcel.readString()

        votes = parcel.readInt()
        timeCreatedText = parcel.readString()
        date = parcel.readSerializable() as Date
    }

    fun setDate() {
        date = inputFormat.parse(timeCreated)
    }

    fun getTimestamp(): Long {
        calendar.time = date
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        return calendar.timeInMillis
    }

    fun setTimeCreatedText() {
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = date

        timeCreatedText = TimeAgo.using(calendar.timeInMillis).capitalize()
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