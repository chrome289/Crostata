package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_home_card.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.modelView.HomeFeedViewModel
import xyz.siddharthseth.crostata.util.recyclerView.RecyclerViewListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AdapterPost(view: View, homeFeedViewModel: HomeFeedViewModel)
    : RecyclerView.ViewHolder(view), View.OnClickListener {

    private var _tag = "AdapterPost"

    private var listener: RecyclerViewListener = homeFeedViewModel

    override fun onClick(v: View?) {
    }

    fun init(post: Post) {
        Log.v(_tag, "sdfsdfsfsdfs")
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

        itemView.votesTotal.text = post.votes.toString()

        itemView.upVoteButton.setImageResource(if (post.opinion == 1) R.drawable.up2 else R.drawable.up3)
        itemView.upVoteButton.setOnClickListener { handleVoteClick(post, 1) }

        itemView.downVoteButton.setImageResource(if (post.opinion == -1) R.drawable.up1 else R.drawable.up3)
        itemView.downVoteButton.setOnClickListener { handleVoteClick(post, -1) }
    }

    private fun handleVoteClick(post: Post, newValue: Int) {
        if (post.opinion == newValue) {
            listener.onClearVote(post.postId).subscribe(
                    { voteTotal: VoteTotal ->
                        Log.v(_tag, "success :" + voteTotal.success)
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
                                Log.v(_tag, "success :" + voteTotal.success)
                                if (voteTotal.success) {
                                    post.votes = voteTotal.total
                                    post.opinion = newValue
                                    this.init(post)
                                }
                            }
                            , { error -> error.printStackTrace() })
        }
    }
}