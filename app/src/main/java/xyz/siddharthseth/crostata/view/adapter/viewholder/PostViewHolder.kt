package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_post_card.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.util.recyclerView.PostItemListener

class PostViewHolder(view: View, private val postItemListener: PostItemListener)
    : RecyclerView.ViewHolder(view), View.OnClickListener {

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.contentImage, R.id.contentText, R.id.commentButton, R.id.title -> {
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

        itemView.timeText.text = post.timeCreatedText

        itemView.contentText.text = post.text
        itemView.contentText.setOnClickListener { this.onClick(it) }

        itemView.votesTotal.text = "${post.votes} votes"
        itemView.votesTotal.setTextColor(
                when {
                    post.opinion == 0 -> extraDarkGrey
                    post.opinion == 1 -> upVoteColor
                    else -> downVoteColor
                }
        )

        itemView.commentsTotal.text = "${post.comments} comments"
    }

    fun loadImages(post: Post) {
        if (post.contentType == "TO") {
            itemView.contentImage.visibility = View.GONE
            postItemListener.clearPostedImageGlide(itemView.contentImage)
        } else {
            itemView.contentImage.visibility = View.VISIBLE
            itemView.contentImage.setOnClickListener { postItemListener.openProfile(post.creatorId) }
            postItemListener.loadPostedImage(post, 640, itemView.contentImage)
            itemView.contentImage.setOnClickListener(this)
            //itemView.imageView.requestLayout()
        }
        //Log.v(TAG, post.glideUrlProfileThumb.toStringUrl())
        postItemListener.loadProfileImage(post, 128, itemView.profileImage)
    }

    fun clearImages() {
        postItemListener.clearPostedImageGlide(itemView.contentImage)
        postItemListener.clearPostedImageGlide(itemView.profileImage)
    }
}