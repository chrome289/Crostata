package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_home_card.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.util.recyclerView.listeners.PostItemListener

class PostViewHolder(view: View, private val postItemListener: PostItemListener)
    : RecyclerView.ViewHolder(view), View.OnClickListener {

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.imageView, R.id.commentButton, R.id.textPost -> {
                    postItemListener.openFullPost(adapterPosition)
                }
                R.id.reportButton -> {

                }
                R.id.downVoteButton -> {
                    postItemListener.handleVote(adapterPosition, 1)
                }
                R.id.upVoteButton -> {
                    postItemListener.handleVote(adapterPosition, -1)
                }
            }
        }
    }

    private var TAG = javaClass.simpleName
    private val voteColor = postItemListener.voteColorTint
    private val reportColor = postItemListener.reportColorTint
    private val extraDarkGrey = postItemListener.extraDarkGrey

    fun init(post: Post) {

        itemView.profileName.text = post.creatorName
        itemView.profileName.setOnClickListener { postItemListener.openProfile(post.creatorId) }

        itemView.timeTextView.text = post.timeCreatedText

        itemView.votesTotal.text = "${post.votes} votes"
        itemView.votesTotal.setTextColor(
                if (post.opinion == 0) extraDarkGrey
                else voteColor
        )

        itemView.commentsTotal.text = "${post.comments} comments"

        /* itemView.upVoteButton.setOnClickListener { this.onClick(it) }
         itemView.upVoteButton.imageTintList =
                 (if (post.opinion == 1) voteColor
                 else greyColor)

         itemView.downVoteButton.setOnClickListener { }
         itemView.downVoteButton.imageTintList =
                 (if (post.opinion == -1) voteColor
                 else greyColor)

         itemView.commentButton.setOnClickListener { this.onClick(it) }

         if (post.creatorId == LoggedSubject.birthId) {
             itemView.reportButton.visibility = View.GONE
         } else {
             itemView.reportButton.visibility = View.VISIBLE
             itemView.reportButton.setOnClickListener { this.onClick(it) }
         }*/

        itemView.textPost.text = post.text
        itemView.textPost.setOnClickListener { this.onClick(it) }
    }

    fun loadImages(post: Post) {
        if (post.contentType == "TO") {
            itemView.imageView.visibility = View.GONE
            postItemListener.clearPostedImageGlide(itemView.imageView)
        } else {
            itemView.imageView.visibility = View.VISIBLE
            postItemListener.loadPostedImage(post, 640, itemView.imageView)
            itemView.imageView.setOnClickListener(this)
            //itemView.imageView.requestLayout()
        }
        postItemListener.loadProfileImage(post.creatorId, 128, false, itemView.profileImage)
    }

    fun clearImages() {
        postItemListener.clearPostedImageGlide(itemView.imageView)
        postItemListener.clearPostedImageGlide(itemView.profileImage)
    }
}