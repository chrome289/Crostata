package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import xyz.siddharthseth.crostata.R

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

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
}