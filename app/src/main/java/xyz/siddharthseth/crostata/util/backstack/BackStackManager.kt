package xyz.siddharthseth.crostata.util.backstack

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import java.util.Calendar
import kotlin.collections.ArrayList

class BackStackManager(private val mListener: BackStackListener, fragmentManager: FragmentManager) {

    private val backStackList: ArrayList<BackStack> = ArrayList()
    private val fragmentManager: BackStackFragmentManager = BackStackFragmentManager(fragmentManager)
    val TAG: String = javaClass.simpleName

    fun addRootFragment(fragment: Fragment, fragmentTag: String, layoutId: Int) {
        if (!isAdded(fragment)) {
            addRoot(fragment, fragmentTag, layoutId)
        } else if (isAdded(fragment) && isCurrent(fragment)) {
            refreshCurrentRoot()
        } else {
            switchRoot(fragment)
            fragmentManager.switchFragment(fragment)
        }
    }

    fun onBackPressed(): Fragment? {
        val current = backStackList.last()
        val id = current.pop()
        return if (id == null) {
            removeRoot(current)
        } else {
            fragmentManager.popBackStack(id)
            null
        }
    }

    fun addChildFragment(fragment: Fragment, layoutId: Int) {
        val id = Calendar.getInstance().timeInMillis.toString() + fragment::class.java.simpleName
        //Log.v(TAG, "id --$id")
        val backStack = backStackList.last()
        backStack.push(id)

        fragmentManager.addChildFragment(fragment, layoutId, id)
    }

    private fun removeRoot(backStack: BackStack): Fragment? {
        backStackList.remove(backStack)

        return if (backStackList.isEmpty()) {
            mListener.finishActivity()
            null
        } else {
            val newRoot = backStackList.last()
            fragmentManager.switchFragment(newRoot.rootFragment)
            newRoot.rootFragment
        }
    }

    private fun addRoot(fragment: Fragment, fragmentTag: String, layoutId: Int) {
        fragmentManager.addFragment(fragment, fragmentTag, layoutId)

        val backStack = BackStack(fragment)
        backStackList.add(backStack)
    }

    private fun switchRoot(fragment: Fragment) {
        for (i in 0 until backStackList.size) {
            val backStack = backStackList[i]
            if (backStack.rootFragment === fragment) {
                backStackList.removeAt(i)
                backStackList.add(backStack)
                break
            }
        }
    }

    private fun refreshCurrentRoot() {
        mListener.tabReselected()
    }

    private fun isAdded(fragment: Fragment): Boolean {
        for (backStack in backStackList) {
            if (backStack.rootFragment === fragment) {
                return true
            }
        }
        return false
    }

    private fun isCurrent(fragment: Fragment): Boolean {
        val backStack = backStackList.last()
        return backStack.rootFragment === fragment
    }
}
