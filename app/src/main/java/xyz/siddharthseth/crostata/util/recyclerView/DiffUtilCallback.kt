package xyz.siddharthseth.crostata.util.recyclerView

import android.support.v7.util.DiffUtil
import xyz.siddharthseth.crostata.data.model.Post

class DiffUtilCallback(val postList: ArrayList<Post>, val newPosts: ArrayList<Post>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return postList.get(oldItemPosition).postId.equals(newPosts.get(newItemPosition).postId)
    }

    override fun getOldListSize(): Int {
        return postList.size
    }

    override fun getNewListSize(): Int {
        return newPosts.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return postList.get(oldItemPosition).postId.equals(newPosts.get(newItemPosition).postId)
    }
}