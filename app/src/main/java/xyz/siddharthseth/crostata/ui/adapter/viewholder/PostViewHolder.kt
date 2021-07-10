package xyz.siddharthseth.crostata.ui.adapter.viewholder

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.databinding.ViewCardFeedPostBinding
import xyz.siddharthseth.crostata.di.module.service.GlideApp
import xyz.siddharthseth.crostata.domain.model.remote.FeedPost
import xyz.siddharthseth.crostata.util.TimeConverter
import java.util.regex.Pattern


class PostViewHolder(
    private val binding: ViewCardFeedPostBinding,
    private val timeConverter: TimeConverter,
    val sharedPreferencesDao: SharedPreferencesDao
) : RecyclerView.ViewHolder(binding.root) {
    fun execute(feedPost: FeedPost) {
        binding.constraintLayoutFeedPostCard.setOnClickListener {
            val intent: Intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("crostata://siddharthseth.xyz/post/@" + feedPost.postId)
                )
            startActivity(binding.root.context, intent, null)
        }

        binding.textViewFeedPostCreatorName.text = feedPost.creatorName
        binding.textViewFeedPostCreatorName.setOnClickListener {
            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("crostata://siddharthseth.xyz/user/@" + feedPost.creatorId)
                )
            startActivity(binding.root.context, intent, null)
        }

        binding.textViewFeedPostContent.movementMethod = LinkMovementMethod.getInstance()
        val postContent =
            Html.fromHtml(feedPost.textContent, Html.FROM_HTML_MODE_COMPACT).toString()
                .replace("   ", "\n\n")
        binding.textViewFeedPostContent.text = postContent
        val ampPattern = Pattern.compile("@[(aA-zZ)(0-9)(_.)]+")
        val ampScheme = "crostata://siddharthseth.xyz/user/"
        val hashPattern = Pattern.compile("#[(aA-zZ)(0-9)(_.)]+")
        val hashScheme = "crostata://siddharthseth.xyz/hashtag/"
        Linkify.addLinks(binding.textViewFeedPostContent, ampPattern, ampScheme)
        Linkify.addLinks(binding.textViewFeedPostContent, hashPattern, hashScheme)

        binding.textViewFeedPostCommentCount.text = feedPost.commentCount.toString()
        binding.textViewFeedPostLikeCount.text = feedPost.likeCount.toString()

        binding.textViewFeedPostCreatedAt.text =
            timeConverter.getFormattedDate(feedPost.createdAt) + "  " + timeConverter.getFormattedTime(
                feedPost.createdAt
            )

        if (feedPost.mediaId?.length ?: 0 > 0) {
            binding.cardFeedPostContent.visibility = View.VISIBLE

            GlideApp.with(binding.root.context)
                .load(feedPost.mediaId)
                .into(binding.imageViewFeedPostContent)
        } else {
            binding.cardFeedPostContent.visibility = View.GONE
        }
        if (feedPost.creatorImage?.length ?: 0 > 0) {
            GlideApp.with(binding.root.context)
                .load(feedPost.creatorImage)
                .into(binding.imageViewFeedPostProfileImage)
        }
    }
}