package xyz.siddharthseth.crostata.util.diffUtil

import android.support.v7.util.DiffUtil
import xyz.siddharthseth.crostata.data.model.retrofit.Comment

class CommentDiffUtilCallback(private val commentList: ArrayList<Comment>, private val newComments: ArrayList<Comment>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return commentList[oldItemPosition]._id == newComments[newItemPosition]._id
    }

    override fun getOldListSize(): Int {
        return commentList.size
    }

    override fun getNewListSize(): Int {
        return newComments.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return commentList[oldItemPosition] == newComments[newItemPosition]
    }
}

