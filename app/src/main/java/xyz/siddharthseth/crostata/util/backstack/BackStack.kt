package xyz.siddharthseth.crostata.util.backstack

import android.support.v4.app.Fragment

class BackStack(val rootFragment: Fragment) {
    private val fragmentIdList: ArrayList<String> = ArrayList()

    fun pop(): String? {
        return if (fragmentIdList.isEmpty()) null else fragmentIdList.removeAt(fragmentIdList.size - 1)
    }

    fun push(id: String) {
        fragmentIdList.add(id)
    }
}