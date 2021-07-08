package xyz.siddharthseth.crostata.domain.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    var sessionId: String = "",
    var userId: String = "",
    var type: Int = xyz.siddharthseth.crostata.domain.model.Session.TYPE_RUN,
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

    var northEastBound: MutableList<Float> = ArrayList(),
    var southWestBound: MutableList<Float> = ArrayList(),

    var summaryPolyLine: String = "",
)