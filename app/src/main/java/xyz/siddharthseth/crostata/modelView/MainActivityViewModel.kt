package xyz.siddharthseth.crostata.modelView

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import xyz.siddharthseth.crostata.R

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var lastSelectedId = R.id.home
    var isInitialized = false
    val TAG = this::class.java.simpleName
    val fragmentCustomStack = ArrayList<Int>()
    var addToBackStack: Boolean = true

    internal fun getToolbarTitle(fragmentId: Int): String {
        val context: Context = getApplication()
        when (fragmentId) {
            R.id.home -> return context.resources.getString(R.string.toolbar_home)
            R.id.community -> return context.resources.getString(R.string.toolbar_community)
            R.id.profile -> return context.resources.getString(R.string.toolbar_profile)
        }
        return ""
    }

    internal fun addToFragmentStack(fragmentId: Int) {
        if (addToBackStack) {
            val position = fragmentCustomStack.indexOf(fragmentId)
            if (position > -1)
                fragmentCustomStack.removeAt(position)
            fragmentCustomStack.add(fragmentId)
            var string = ""
            for (value in fragmentCustomStack)
                string += value.toString() + "--"
        } else
            addToBackStack = true
        lastSelectedId = fragmentId
    }
}