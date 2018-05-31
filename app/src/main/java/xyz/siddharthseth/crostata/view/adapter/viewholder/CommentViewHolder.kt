package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_comment_card.view.*
import xyz.siddharthseth.crostata.data.model.retrofit.Comment
import xyz.siddharthseth.crostata.util.recyclerView.CommentItemListener

class CommentViewHolder(view: View, private val commentItemListener: CommentItemListener)
    : RecyclerView.ViewHolder(view) {

    private val TAG = javaClass.simpleName
    fun init(comment: Comment) {
        itemView.profileName.text = comment.name
        itemView.profileName.setOnClickListener {
            commentItemListener.openProfile(adapterPosition)
        }
        itemView.title.text = comment.text

        itemView.timeText.text = comment.timeCreatedText
    }

    fun loadImages(comment: Comment) {
        commentItemListener.loadProfileImage(comment, 128, itemView.profileImage)
        itemView.profileImage.setOnClickListener {
            commentItemListener.openProfile(adapterPosition)
        }
    }
}
