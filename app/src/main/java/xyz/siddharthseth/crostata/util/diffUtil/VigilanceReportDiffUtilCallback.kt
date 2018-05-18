package xyz.siddharthseth.crostata.util.diffUtil

import android.support.v7.util.DiffUtil
import xyz.siddharthseth.crostata.data.model.retrofit.Report

class VigilanceReportDiffUtilCallback(private val oldList: ArrayList<Report>, private val newList: ArrayList<Report>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]._id == newList[newItemPosition]._id
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}