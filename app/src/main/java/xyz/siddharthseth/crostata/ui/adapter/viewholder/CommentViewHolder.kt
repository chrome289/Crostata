package xyz.siddharthseth.crostata.ui.adapter.viewholder

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.databinding.ViewCardPostCommentBinding
import xyz.siddharthseth.crostata.di.module.service.GlideApp
import xyz.siddharthseth.crostata.domain.model.remote.Post
import xyz.siddharthseth.crostata.util.TimeConverter
import java.util.regex.Pattern


class CommentViewHolder(
    private val binding: ViewCardPostCommentBinding,
    private val timeConverter: TimeConverter,
    val sharedPreferencesDao: SharedPreferencesDao
) : RecyclerView.ViewHolder(binding.root) {
    fun execute(reply: Post) {
        binding.constraintLayoutFeedPostCard.setOnClickListener {
            val intent: Intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("crostata://siddharthseth.xyz/post/@" + reply.postId)
                )
            ContextCompat.startActivity(binding.root.context, intent, null)
        }

        binding.textViewFeedPostCreatorName.text = reply.creatorName
        binding.textViewFeedPostCreatorName.setOnClickListener {
            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("crostata://siddharthseth.xyz/user/@" + reply.creatorId)
                )
            ContextCompat.startActivity(binding.root.context, intent, null)
        }

        binding.textViewFeedPostContent.movementMethod = LinkMovementMethod.getInstance()
        val postContent =
            Html.fromHtml(reply.textContent, Html.FROM_HTML_MODE_COMPACT).toString()
                .replace("   ", "\n\n")
        binding.textViewFeedPostContent.text = postContent
        val ampPattern = Pattern.compile("@[(aA-zZ)(0-9)(_.)]+")
        val ampScheme = "crostata://siddharthseth.xyz/user/"
        val hashPattern = Pattern.compile("#[(aA-zZ)(0-9)(_.)]+")
        val hashScheme = "crostata://siddharthseth.xyz/hashtag/"
        Linkify.addLinks(binding.textViewFeedPostContent, ampPattern, ampScheme)
        Linkify.addLinks(binding.textViewFeedPostContent, hashPattern, hashScheme)

        binding.textViewFeedPostCommentCount.text = reply.commentCount.toString()
        binding.textViewFeedPostLikeCount.text = reply.likeCount.toString()

        binding.textViewFeedPostCreatedAt.text =
            timeConverter.getFormattedDate(reply.createdAt) + "  " + timeConverter.getFormattedTime(
                reply.createdAt
            )

        if (reply.mediaId?.length ?: 0 > 0) {
            binding.cardFeedPostContent.visibility = View.VISIBLE

            GlideApp.with(binding.root.context)
                .load(reply.mediaId)
                .into(binding.imageViewFeedPostContent)
        } else {
            binding.cardFeedPostContent.visibility = View.GONE
        }
        if (reply.creatorImage?.length ?: 0 > 0) {
            GlideApp.with(binding.root.context)
                .load(reply.creatorImage)
                .into(binding.imageViewFeedPostProfileImage)
        }
    }
}
