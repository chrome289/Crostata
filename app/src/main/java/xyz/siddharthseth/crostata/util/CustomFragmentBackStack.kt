package xyz.siddharthseth.crostata.util

import android.support.v4.app.Fragment
import android.util.Log
import xyz.siddharthseth.crostata.R

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
                    removeChildrenFragments(position)
                    break
                }
            }
            return true
        }

        private fun removeChildrenFragments(position: Int) {
            val listIterator = backStack.listIterator()
            while (listIterator.hasNext()) {
                if (listIterator.nextIndex() >= position) {
                    val element = listIterator.next()
                    if (element.fragmentId == R.id.viewPost
                            || element.fragmentId == R.id.profile2) {
                        listIterator.remove()
                    }
                } else {
                    listIterator.next()
                }
            }
        }

        fun pushFragment(fragmentEntry: FragmentEntry) {
            backStack.add(fragmentEntry)
            if (fragmentEntry.fragmentId != R.id.viewPost
                    && fragmentEntry.fragmentId != R.id.profile2)
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