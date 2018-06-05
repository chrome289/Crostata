package xyz.siddharthseth.crostata.util.diffUtil

import android.support.v7.util.DiffUtil
import xyz.siddharthseth.crostata.data.model.retrofit.SearchResult

class SearchResultDiffUtilCallback(private val resultList: ArrayList<SearchResult>, private val newResultList: ArrayList<SearchResult>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return resultList[oldItemPosition].birthId == newResultList[newItemPosition].birthId
    }

    override fun getOldListSize(): Int {
        return resultList.size
    }

    override fun getNewListSize(): Int {
        return newResultList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return resultList[oldItemPosition] == newResultList[newItemPosition]
    }
}