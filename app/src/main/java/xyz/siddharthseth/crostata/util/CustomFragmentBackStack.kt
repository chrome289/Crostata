package xyz.siddharthseth.crostata.util

import android.support.v4.app.Fragment
import android.util.Log

class CustomFragmentBackStack {

    companion object {
        private val backStack = ArrayList<FragmentEntry>()
        private val TAG: String = CustomFragmentBackStack::class.java.simpleName

        fun getSize(): Int {
            return backStack.size
        }

        private fun removeFragmentById(fragmentId: Int): Boolean {
            for (position in 0 until backStack.size - 1) {
                val fragmentEntry = backStack[position]
                if (fragmentEntry.fragmentId == fragmentId) {
                    backStack.removeAt(position)
                    break
                }
            }
            return true
        }

        fun pushFragment(fragmentEntry: FragmentEntry) {
            backStack.add(fragmentEntry)
            removeFragmentById(fragmentEntry.fragmentId)
        }

        fun popFragment() {
            backStack.removeAt(getSize() - 1)
        }

        fun getFragmentEntryById(fragmentId: Int): Fragment? {
            for (fragmentEntry in backStack) {
                if (fragmentEntry.fragmentId == fragmentId)
                    return fragmentEntry.fragment
            }
            return null
        }

        fun getLastFragmentEntry(): FragmentEntry {
            return backStack[getSize() - 1]
        }

        fun printStack() {
            var str = ""
            for (fragmentEntry in backStack)
                str += fragmentEntry.fragmentId.toString() + " "
            Log.v(TAG, " Stack -> $str")
        }
    }

    class FragmentEntry(val fragment: Fragment, val fragmentId: Int)
}