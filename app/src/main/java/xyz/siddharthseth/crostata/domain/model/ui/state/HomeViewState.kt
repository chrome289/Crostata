package xyz.siddharthseth.crostata.domain.model.ui.state

data class HomeViewState(
    val weatherSummary: String,
    val weatherHumidity: String,
    val weatherPrecipitation: String,
    val weatherWind: String,
    val weatherFeelsLikeTemperature: String,
    val weatherTemperature: String,
    val weatherIconDrawableId: String,

    val recentSessionType: Int,

    val workoutCount: Int = 0,
    val isWorkoutSelected: Boolean = false,
    val isCouchFiverSelected: Boolean = false,
    val selectedWorkout: String,

    val routeCount: Int = 0,
    val isRouteSelected: Boolean = false,
    val selectedRoute: String,

    val isGoalSelected: Boolean = false,
    val selectedGoal: String,

    val profileNameText: String = "",
    var gender: Int = 0,
    val toolbarProfileImageUrl: String = "",
    val monthSummary: String = "",
    val monthSummaryDrawableId: Int = 0,
    val greeting: String = "",

    val isDetailVisible: Boolean,
    val isFetchingWeather: Boolean,
    val isTrackingSession: Boolean
)