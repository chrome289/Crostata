package xyz.siddharthseth.crostata.util.viewModel

import android.arch.lifecycle.MutableLiveData

interface BusyLoaderListener {
    fun showLoader(isShown: Boolean)
    fun showError(isShown: Boolean)
    fun showAnimation(isShown: Boolean)

    fun setLoaderVisibility(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean)

    fun isNetAvailable()

    var mutableNetStatusChanged: MutableLiveData<Boolean>
}