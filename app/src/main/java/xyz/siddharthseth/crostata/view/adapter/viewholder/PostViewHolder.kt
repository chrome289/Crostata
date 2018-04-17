package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.recyclerview_home_card.view.*
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.util.recyclerView.PostRecyclerViewListener
import xyz.siddharthseth.crostata.viewmodel.fragment.HomeFeedViewModel
import java.text.SimpleDateFormat
import java.util.*

class PostViewHolder(view: View, homeFeedViewModel: HomeFeedViewModel)
    : RecyclerView.ViewHolder(view) {

    private var TAG = javaClass.simpleName

    private var listenerPost: PostRecyclerViewListener = homeFeedViewModel
    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

    private val voteColor = ColorStateList.valueOf(listenerPost.voteColorTint)
    val reportColor: ColorStateList? = ColorStateList.valueOf(listenerPost.reportColorTint)
    private val greyColor = ColorStateList.valueOf(listenerPost.greyUnselected)

    fun init(post: Post) {

        itemView.nameTextView.text = post.creatorName.toUpperCase()
        itemView.nameTextView.setOnClickListener { listenerPost.openProfile(post.creatorId) }

        if (post.contentType == "TO") {
            itemView.imageView.visibility = View.GONE
            clearView()
        } else {
            itemView.imageView.visibility = View.VISIBLE
            listenerPost.loadPostedImage(post, itemView.imageView)
            itemView.imageView.requestLayout()
            itemView.imageView.setOnClickListener {
                Log.v(TAG, "itemview click listener")
                listenerPost.openFullPost(post)
            }
        }

        listenerPost.loadProfileImage(post.creatorId, itemView.profileImage)

        itemView.textPost.text = post.text
        itemView.textPost.setOnClickListener { listenerPost.openFullPost(post) }

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = inputFormat.parse(post.timeCreated)

        itemView.timeTextView.text = TimeAgo.using(calendar.timeInMillis).capitalize()

        itemView.votesTotal.text = post.votes.toString()
        itemView.votesTotal.setTextColor(
                if (post.opinion == 0) greyColor
                else voteColor
        )
        Log.v(TAG, "post.opinion " + post.opinion)

        itemView.upVoteButton.setOnClickListener { handleVoteClick(post, 1) }
        itemView.upVoteButton.imageTintList =
                (if (post.opinion == 1) voteColor
                else greyColor)

        itemView.downVoteButton.setOnClickListener { handleVoteClick(post, -1) }
        itemView.downVoteButton.imageTintList =
                (if (post.opinion == -1) voteColor
                else greyColor)

        itemView.commentsTotal.text = post.comments.toString()
        itemView.commentButton.setOnClickListener { listenerPost.openFullPost(post) }

        if (post.creatorId == LoggedSubject.birthId) {
            itemView.reportButton.visibility = View.GONE
        } else {
            itemView.reportButton.visibility = View.VISIBLE
            itemView.reportButton.setOnClickListener { }
        }
    }

    private fun handleVoteClick(post: Post, newValue: Int) {
        if (post.opinion == newValue) {
            listenerPost.onClearVote(post._id).subscribe(
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
            listenerPost.onVoteButtonClick(post._id, newValue)
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
        listenerPost.clearPostedImageGlide(itemView.imageView)
    }
}