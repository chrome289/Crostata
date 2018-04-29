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
            }
        }
    }

    private var TAG = javaClass.simpleName
    private val upVoteColor = postItemListener.upVoteColorTint
    private val downVoteColor = postItemListener.downVoteColorTint
    private val extraDarkGrey = postItemListener.extraDarkGrey

    fun init(post: Post) {

        itemView.profileName.text = post.creatorName
        itemView.profileName.setOnClickListener { postItemListener.openProfile(post.creatorId) }

        itemView.timeTextView.text = post.timeCreatedText

        itemView.votesTotal.text = "${post.votes} votes"
        itemView.votesTotal.setTextColor(
                when {
                    post.opinion == 0 -> extraDarkGrey
                    post.opinion == 1 -> upVoteColor
                    else -> downVoteColor
                }
        )

        itemView.commentsTotal.text = "${post.comments} comments"

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
        //Log.v(TAG, post.glideUrlProfileThumb.toStringUrl())
        postItemListener.loadProfileImage(post, 128, itemView.profileImage)
    }

    fun clearImages() {
        postItemListener.clearPostedImageGlide(itemView.imageView)
        postItemListener.clearPostedImageGlide(itemView.profileImage)
    }
}