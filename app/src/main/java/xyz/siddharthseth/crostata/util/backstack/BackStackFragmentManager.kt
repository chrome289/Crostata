package xyz.siddharthseth.crostata.util.backstack

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

class BackStackFragmentManager(private val fragmentManager: FragmentManager) {
    private var lastAddedFragment: Fragment? = null

    fun switchFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.show(fragment)
        transaction.hide(lastAddedFragment)
        transaction.commit()

        lastAddedFragment = fragment
    }

    fun addChildFragment(fragment: Fragment, layoutId: Int, tag: String) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(layoutId, fragment, tag)
        transaction.commit()
    }

    fun addFragment(fragment: Fragment, fragmentTag: String, layoutId: Int) {
        val transaction = fragmentManager.beginTransaction()

        if (!fragment.isAdded) {
            transaction.add(layoutId, fragment, fragmentTag)
        } else {
            transaction.show(fragment)
        }

        if (lastAddedFragment != null) {
            transaction.hide(lastAddedFragment)
        }

        transaction.commit()

        lastAddedFragment = fragment
    }

    fun popBackStack(tag: String) {
        val fragment = fragmentManager.findFragmentByTag(tag)
        val transaction = fragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()
    }
}