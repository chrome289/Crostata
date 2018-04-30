package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_comment_card.view.*
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.util.recyclerView.CommentItemListener

class CommentViewHolder(view: View, private val commentItemListener: CommentItemListener)
    : RecyclerView.ViewHolder(view) {

    private val TAG = javaClass.simpleName
    fun init(comment: Comment) {
        itemView.nameTextView.text = comment.name
        itemView.textPost.text = comment.text

        itemView.timeTextView.text = comment.timeCreatedText
    }

    fun loadImages(comment: Comment) {
        commentItemListener.loadProfileImage(comment, 128, itemView.profileImage)
    }
}
