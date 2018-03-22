package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.recyclerview_home_card.view.*
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.modelView.HomeFeedViewModel
import xyz.siddharthseth.crostata.util.recyclerView.RecyclerViewListener
import java.text.SimpleDateFormat
import java.util.*

class AdapterPost(view: View, homeFeedViewModel: HomeFeedViewModel)
    : RecyclerView.ViewHolder(view) {

    private var TAG = "AdapterPost"

    private var listener: RecyclerViewListener = homeFeedViewModel
    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

    val upVoteColor = ColorStateList.valueOf(listener.upVoteColorTint)
    val downVoteColor = ColorStateList.valueOf(listener.downVoteColorTint)
    val commentColor = ColorStateList.valueOf(listener.commentColorTint)
    val reportColor = ColorStateList.valueOf(listener.reportColorTint)
    val greyColor = ColorStateList.valueOf(listener.greyUnselected)

    fun init(post: Post) {

        itemView.nameTextView.text = post.creatorName.toUpperCase()

        if (post.contentType == "TO") {
            itemView.imageView.visibility = View.GONE
            clearView()
        } else {
            itemView.imageView.visibility = View.VISIBLE
            itemView.imageView.setOnClickListener { listener.openFullPost(post) }
            listener.loadPostedImage(post, itemView.imageView)
            itemView.imageView.requestLayout()
        }

        listener.loadProfileImage(post.creatorId, itemView.profileImage)

        itemView.textPost.text = post.text
        itemView.textPost.setOnClickListener { listener.openFullPost(post) }

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = inputFormat.parse(post.timeCreated)

        itemView.timeTextView.text = TimeAgo.using(calendar.timeInMillis).toUpperCase()

        itemView.votesTotal.text = post.votes.toString()
        itemView.votesTotal.setTextColor(when (post.opinion) {
            1 -> upVoteColor
            -1 -> downVoteColor
            else -> greyColor
        })

        itemView.upVoteButton.setOnClickListener { handleVoteClick(post, 1) }
        itemView.upVoteButton.imageTintList =
                (if (post.opinion == 1) upVoteColor
                else greyColor)

        itemView.downVoteButton.setOnClickListener { handleVoteClick(post, -1) }
        itemView.downVoteButton.imageTintList =
                (if (post.opinion == -1) downVoteColor
                else greyColor)

        Log.v(TAG, post.postId + " loaded")
    }

    private fun handleVoteClick(post: Post, newValue: Int) {
        if (post.opinion == newValue) {
            listener.onClearVote(post.postId).subscribe(
                    { voteTotal: VoteTotal ->
                        Log.v(TAG, "success :" + voteTotal.success)
                        if (voteTotal.success) {
                            post.opinion = 0
                            post.votes = voteTotal.total
                            this.init(post)
                        }
                    }
                    , { error -> error.printStackTrace() })
        } else {
            listener.onVoteButtonClick(post.postId, newValue)
                    .subscribe(
                            { voteTotal: VoteTotal ->
                                Log.v(TAG, "success :" + voteTotal.success)
                                if (voteTotal.success) {
                                    post.votes = voteTotal.total
                                    post.opinion = newValue
                                    this.init(post)
                                }
                            }
                            , { error -> error.printStackTrace() })
        }
    }

    private fun clearView() {
        listener.clearPostedImageGlide(itemView.imageView)
    }
}