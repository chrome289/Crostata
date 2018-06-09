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
                R.id.contentText, R.id.contentImage -> {
                    postItemListener.openFullPost(adapterPosition)
                }
                R.id.profileName, R.id.profileImage -> {
                    postItemListener.openProfile(adapterPosition)
                }
                R.id.likeButton, R.id.likesTotal -> {
                    postItemListener.handleLike(adapterPosition)
                }
                R.id.commentButton, R.id.commentTotal -> {
                    postItemListener.openFullPost(adapterPosition)
                }
            }
        }
    }

    private var TAG = javaClass.simpleName
    private val likeColorTint = postItemListener.likeColorTint
    private val extraDarkGrey = postItemListener.grey900
    private val grey500 = postItemListener.grey500
    private val grey900 = postItemListener.grey900

    fun init(post: Post) {
        if (post.isCensored || post.isGenerated) {
            itemView.approveImage.visibility = View.VISIBLE
            itemView.approveText.visibility = View.VISIBLE
        } else {
            itemView.approveImage.visibility = View.GONE
            itemView.approveText.visibility = View.GONE
        }

        itemView.profileName.text = post.creatorName
        itemView.profileName.setOnClickListener { this.onClick(it) }

        itemView.timeText.text = post.timeCreatedText

        itemView.contentText.text = post.text
        itemView.contentText.setOnClickListener { this.onClick(it) }

        itemView.likeButton.setOnClickListener { this.onClick(it) }
        itemView.likeButton.imageTintList = if (post.opinion == 1) likeColorTint else grey500

        itemView.likesTotal.text = "${post.likes} likes"
        itemView.likesTotal.setTextColor(if (post.opinion == 1) likeColorTint else grey900)

        itemView.commentButton.setOnClickListener { this.onClick(it) }
        itemView.commentsTotal.text = "${post.comments} comments"
    }

    fun loadImages(post: Post) {
        if (post.contentType == "TO") {
            itemView.contentImage.visibility = View.GONE
            postItemListener.clearPostedImageGlide(itemView.contentImage)
        } else {
            itemView.contentImage.visibility = View.VISIBLE
            itemView.contentImage.setOnClickListener { this.onClick(it) }
            postItemListener.loadPostedImage(post, 640, itemView.contentImage)
            //itemView.imageView.requestLayout()
        }
        //Log.v(TAG, post.glideUrlProfileThumb.toStringUrl()).
        postItemListener.loadProfileImage(post, 128, itemView.profileImage)
    }

    fun clearImages() {
        postItemListener.clearPostedImageGlide(itemView.contentImage)
        postItemListener.clearPostedImageGlide(itemView.profileImage)
    }
}