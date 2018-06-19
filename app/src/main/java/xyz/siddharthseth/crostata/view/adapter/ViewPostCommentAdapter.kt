package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.Comment
import xyz.siddharthseth.crostata.util.recyclerView.CommentItemListener
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.CommentViewHolder
import xyz.siddharthseth.crostata.view.adapter.viewholder.FooterViewHolder

/**
 *adapter for comments
 */
class ViewPostCommentAdapter(private val commentItemListener: CommentItemListener, private val footerListener: FooterListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemView) {
            CommentViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_comment_card, parent, false)
                    , commentItemListener)
        } else {
            FooterViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.recyclerview_loading_footer, parent, false)
                    , footerListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < commentList.size - 1)
            itemView
        else
            footerView
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentViewHolder) {
            holder.init(commentList[position])
            holder.loadImages(commentList[position])
        } else {
            holder as FooterViewHolder
            holder.init(isSecondLoaderVisible, isErrorVisible, isEndVisible)
        }
    }

    var commentList = ArrayList<Comment>()
    var isSecondLoaderVisible = false
    var isErrorVisible = false
    var isEndVisible: Boolean = false
    //static stuff
    companion object {
        private const val itemView = 1
        private const val footerView = 2
    }
}