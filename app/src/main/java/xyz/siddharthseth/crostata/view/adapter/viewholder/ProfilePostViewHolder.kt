package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.recyclerview_profile_post.view.*
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

class ProfilePostViewHolder(itemView: View, private val profileViewModel: ProfileViewModel)
    : RecyclerView.ViewHolder(itemView) {

    val TAG: String = javaClass.simpleName
    private var listenerPost: ProfileViewModel = profileViewModel

    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

    private val voteColor = listenerPost.voteColorTint
    private val reportColor = listenerPost.reportColorTint
    private val extraDarkGrey = listenerPost.extraDarkGrey

    fun init(post: Post) {

        itemView.profileName.text = post.creatorName.toUpperCase()
        profileViewModel.loadProfileImage(post.creatorId, 128, true, itemView.profileImage)

        // Log.v(TAG, post.contentType + "ww")
        if (post.contentType == "TO") {
            itemView.imageView.visibility = View.GONE
            clearView()
        } else {
            itemView.imageView.visibility = View.VISIBLE
            profileViewModel.loadPostedImage(post, 1080, itemView.imageView)
            //  itemView.imageView.setOnClickListener { listenerPost.openFullPost(post) }
            itemView.imageView.requestLayout()
        }

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = inputFormat.parse(post.timeCreated)

        itemView.timeTextView.text = TimeAgo.using(calendar.timeInMillis).capitalize()

        itemView.votesTotal.text = post.votes.toString()
        itemView.votesTotal.setTextColor(
                if (post.opinion == 0) extraDarkGrey
                else voteColor
        )
        // Log.v(TAG, "post.opinion " + post.opinion)

        itemView.upVoteButton.setOnClickListener { handleVoteClick(post, 1) }
        itemView.upVoteButton.imageTintList =
                (if (post.opinion == 1) voteColor
                else extraDarkGrey)

        itemView.downVoteButton.setOnClickListener { handleVoteClick(post, -1) }
        itemView.downVoteButton.imageTintList =
                (if (post.opinion == -1) voteColor
                else extraDarkGrey)

        itemView.commentsTotal.text = post.comments.toString()
        // itemView.commentButton.setOnClickListener { listenerPost.openFullPost(post) }

        if (post.creatorId == LoggedSubject.birthId) {
            itemView.reportButton.visibility = View.GONE
        } else {
            itemView.reportButton.visibility = View.VISIBLE
            itemView.reportButton.setOnClickListener { }
        }

        itemView.textPost.text = post.text
        //itemView.textPost.setOnClickListener { listenerPost.openFullPost(post)

    }

    private fun handleVoteClick(post: Post, newValue: Int) {
        /* if (post.opinion == newValue) {
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
         }*/
    }

    private fun clearView() {
        // listenerPost.clearPostedImageGlide(itemView.imageView)
    }
}