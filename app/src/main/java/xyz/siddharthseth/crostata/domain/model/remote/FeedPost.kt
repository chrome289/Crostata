package xyz.siddharthseth.crostata.domain.model.remote


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedPost(
    val postId: String,
    val creatorId: String,
    val creatorName: String,
    val creatorImage: String?,
    val likeCount: Int,
    val commentCount: Int,
    val textContent: String,
    val mediaId: String?,
    val createdAt: String,
    val updatedAt: String,
)