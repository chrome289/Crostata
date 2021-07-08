package xyz.siddharthseth.crostata.domain.model.remote

data class JsonResponse(
    val statusCode: Int,
    val links: Links,
    val data: Any,
    val error: Error,
) {
    inner class Links(
        val before: String,
        val after: String
    )

    inner class Error(
        val statusCode: Int,
        val clientError: String,
        val serverError: Boolean
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonResponse

        if (statusCode != other.statusCode) return false
        if (links != other.links) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + links.hashCode()
        result = 31 * result + error.hashCode()
        return result
    }
}