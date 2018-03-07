package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.recyclerview_home_card.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.modelView.HomeFeedViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AdapterPost(view: View, private val homeFeedViewModel: HomeFeedViewModel)
    : RecyclerView.ViewHolder(view), View.OnClickListener {

    interface OnRecyclerViewEventListener {
        fun onVoteButtonClick(postId: String)
        fun onDownButtonClick(postId: String)
        fun onCommentButtonClick(postId: String)
        fun onReportButtonClick(postId: String)
        fun loadPostedImage(postId: String, imageView: ImageView)
        fun loadProfileImage(creatorId: String, imageView: ImageView)
    }

    var TAG = "AdapterPost"

    var listener: OnRecyclerViewEventListener = homeFeedViewModel

    override fun onClick(v: View?) {
    }

    fun init(post: Post) {
        itemView.nameTextView.text = post.creatorName
        if (post.contentType == "TO") {
            itemView.imageView.visibility = View.GONE
            itemView.textPost.visibility = View.VISIBLE
        } else {
            itemView.imageView.visibility = View.VISIBLE
            itemView.textPost.visibility = View.VISIBLE

            listener.loadPostedImage(post.postId, itemView.imageView)
        }
        listener.loadProfileImage(post.creatorId, itemView.profileImage)

        itemView.textPost.text = post.text
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        calendar.time = inputFormat.parse(post.timeCreated)

        val dateOutputFormat: DateFormat = SimpleDateFormat("MMM dd, -yy", Locale.US)
        val timeDateFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.US)

        itemView.dateTextView.text = dateOutputFormat.format(calendar.time).replace('-', '\'')
        itemView.timeTextView.text = timeDateFormat.format(calendar.time)

        itemView.votesTotal.text = (post.upVotes - post.downVotes + post.myVote).toString()

        itemView.upVoteButton.setImageResource(if (post.myVote == 1) R.drawable.up2 else R.drawable.up3)
        itemView.upVoteButton.setOnClickListener { listener.onVoteButtonClick(post.postId) }

        itemView.downVoteButton.setImageResource(if (post.myVote == -1) R.drawable.up1 else R.drawable.up3)

    }
}