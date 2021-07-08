package xyz.siddharthseth.crostata.domain.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "sessions")
//@TypeConverters(RoomConverterUtil::class)
@JsonClass(generateAdapter = true)
data class Session(
    @PrimaryKey
    var sessionId: String = "",
    var userId: String = "",
    var type: Int = TYPE_RUN,
    var name: String = "",
    var effort: Int = 6,

    var startDate: String = "",
    var duration: Long = 0L,
    var pauseDuration: Long = 0L,

    var distance: Float = 0f,
    var energy: Float = 0f,

    var totalElevationGain: Float = 0f,
    var totalElevationLoss: Float = 0f,
    var maxElevation: Float = 0f,
    var minElevation: Float = 0f,
    var elevationGrade: Float = 0f,

    var speed: Float = 0f,
    var maxSpeed: Float = 0f,
    var minSpeed: Float = 0f,

    var avgCadence: Int = 0,
    var stepCount: Int = 0,
    var recordCount: Int = 0,

    var isUploaded: Boolean = false,
    var isDeleted: Boolean = false,
    var hasCorrectedElevation: Boolean = false,
    var isSynced: Boolean = false,

    var routeId: String = "",
    var workoutId: String = "",
    var fileId: String = "",


    @Ignore
    var northEastBound: MutableList<Float> = ArrayList(),
    @Ignore
    var southWestBound: MutableList<Float> = ArrayList(),

    @Ignore
    var summaryPolyLine: String = "",
) : Cloneable {
    companion object {
        const val TYPE_RUN = 10101
        const val TYPE_WALK = 10102
        const val TYPE_CYCLE = 10103
        fun cloneList(sessionList: List<Session>): ArrayList<Session> {
            val newList = ArrayList<Session>()
            for (session in sessionList)
                newList.add(session.clone())
            return newList
        }
    }

    //cloneable
    override fun clone(): Session {
        val session = Session(
            sessionId,
            userId,
            type,
            name,
            effort,
            startDate,
            duration,
            pauseDuration,
            distance,
            energy,
            totalElevationGain,
            totalElevationLoss,
            maxElevation,
            minElevation,
            elevationGrade,
            speed,
            maxSpeed,
            minSpeed,
            avgCadence,
            stepCount,
            recordCount,
            isUploaded,
            isDeleted,
            hasCorrectedElevation,
            isSynced,
            routeId,
            workoutId,
            fileId,
            northEastBound,
            southWestBound,
            summaryPolyLine
        )
        return session
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Session

        if (sessionId != other.sessionId) return false
        if (userId != other.userId) return false
        if (type != other.type) return false
        if (name != other.name) return false
        if (startDate != other.startDate) return false
        if (duration != other.duration) return false
        if (pauseDuration != other.pauseDuration) return false
        if (distance != other.distance) return false
        if (energy != other.energy) return false
        if (totalElevationGain != other.totalElevationGain) return false
        if (totalElevationLoss != other.totalElevationLoss) return false
        if (maxElevation != other.maxElevation) return false
        if (minElevation != other.minElevation) return false
        if (speed != other.speed) return false
        if (maxSpeed != other.maxSpeed) return false
        if (minSpeed != other.minSpeed) return false
        if (isSynced != other.isSynced) return false
        if (isDeleted != other.isDeleted) return false
        if (routeId != other.routeId) return false
        if (workoutId != other.workoutId) return false
        if (fileId != other.fileId) return false
        if (northEastBound != other.northEastBound) return false
        if (southWestBound != other.southWestBound) return false
        if (summaryPolyLine != other.summaryPolyLine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sessionId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + type
        result = 31 * result + name.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + pauseDuration.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + energy.hashCode()
        result = 31 * result + totalElevationGain.hashCode()
        result = 31 * result + totalElevationLoss.hashCode()
        result = 31 * result + maxElevation.hashCode()
        result = 31 * result + minElevation.hashCode()
        result = 31 * result + speed.hashCode()
        result = 31 * result + maxSpeed.hashCode()
        result = 31 * result + minSpeed.hashCode()
        result = 31 * result + isSynced.hashCode()
        result = 31 * result + isDeleted.hashCode()
        result = 31 * result + routeId.hashCode()
        result = 31 * result + workoutId.hashCode()
        result = 31 * result + fileId.hashCode()
        result = 31 * result + northEastBound.hashCode()
        result = 31 * result + southWestBound.hashCode()
        result = 31 * result + summaryPolyLine.hashCode()
        return result
    }


}