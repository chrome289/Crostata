package xyz.siddharthseth.crostata.data.model

import android.os.Parcel
import android.os.Parcelable
import xyz.siddharthseth.crostata.data.model.retrofit.ImageMetadata
import java.text.SimpleDateFormat
import java.util.*

class Post() : Comparable<Post>, Parcelable {
    override fun compareTo(other: Post): Int {
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

    var postId = ""
    var creatorName = ""
    var creatorId = ""
    var timeCreated = ""
    var contentType = ""
    var text = ""
    var votes = 0
    private var isCensored = false
    var opinion = 0

    var metadata: ImageMetadata = ImageMetadata()

    constructor(parcel: Parcel) : this() {
        postId = parcel.readString()
        creatorName = parcel.readString()
        creatorId = parcel.readString()
        timeCreated = parcel.readString()
        contentType = parcel.readString()
        text = parcel.readString()
        votes = parcel.readInt()
        isCensored = parcel.readByte() != 0.toByte()
        opinion = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(creatorName)
        parcel.writeString(creatorId)
        parcel.writeString(timeCreated)
        parcel.writeString(contentType)
        parcel.writeString(text)
        parcel.writeInt(votes)
        parcel.writeByte(if (isCensored) 1 else 0)
        parcel.writeInt(opinion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}