package xyz.siddharthseth.crostata.util.diffUtil

import android.support.v7.util.DiffUtil
import xyz.siddharthseth.crostata.data.model.retrofit.Post

class PostDiffUtilCallback(private val postList: ArrayList<Post>, private val newPosts: ArrayList<Post>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return postList[oldItemPosition]._id == newPosts[newItemPosition]._id
    }

    override fun getOldListSize(): Int {
        return postList.size
    }

    override fun getNewListSize(): Int {
        return newPosts.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return postList[oldItemPosition] == newPosts[newItemPosition]
    }
}