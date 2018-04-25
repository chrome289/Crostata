package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.util.recyclerView.listeners.CommentItemListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.CommentViewHolder

class ViewPostCommentAdapter(private val commentItemListener: CommentItemListener) : RecyclerView.Adapter<CommentViewHolder>() {

    var commentList = ArrayList<Comment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_comment_card, parent, false)
                , commentItemListener)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.init(commentList[position])
        holder.loadImages(commentList[position])
    }
}