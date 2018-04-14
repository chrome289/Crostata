package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.util.CustomFragmentBackStack

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var lastSelectedId = R.id.home
    var isInitialized = false
    val TAG: String = this::class.java.simpleName

    internal fun getToolbarTitle(fragmentId: Int): String {
        val context: Context = getApplication()
        when (fragmentId) {
            R.id.home -> return context.resources.getString(R.string.toolbar_home)
            R.id.community -> return context.resources.getString(R.string.toolbar_community)
            R.id.profile -> return context.resources.getString(R.string.toolbar_profile)
            R.id.vigilance -> return context.resources.getString(R.string.toolbar_vigilance)
        }
        return ""
    }

    fun addToFragmentStack(fragmentEntry: CustomFragmentBackStack.FragmentEntry) {
        CustomFragmentBackStack.pushFragment(fragmentEntry)
        lastSelectedId = fragmentEntry.fragmentId
    }

}