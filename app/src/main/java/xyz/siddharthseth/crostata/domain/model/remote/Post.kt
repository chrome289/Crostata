package xyz.siddharthseth.crostata.domain.model.remote


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    val postId: String,
    val creatorId: String,
    val creatorName: String,
    val creatorImage: String?,
    val conversationId: String,
    val replyTo: String?,
    val replyToUser: String?,
    val commentCount: Int,
    val likeCount: Int,
    val textContent: String,
    val mediaId: String?,
    val createdAt: String,
    val updatedAt: String,
)