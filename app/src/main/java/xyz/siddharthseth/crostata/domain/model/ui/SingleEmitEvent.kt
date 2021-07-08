package xyz.siddharthseth.pandoro.domain.model.ui

data class SingleEmitEvent(
    val isSuccessful: Boolean = false,
    val error: String = ""
) {
    companion object {
        const val ERROR_WORKOUT_SAVE_NO_NAME = "no workout name"
        const val ERROR_WORKOUT_SAVE_NO_STINTS = "no stints added"
        const val ERROR_ROUTE_SAVE_NO_NAME = "no route name"
        const val ERROR_ROUTE_SAVE_NO_POINTS = "no points added"
    }
}