package xyz.siddharthseth.crostata.util.viewModel

import android.arch.lifecycle.MutableLiveData

/**
 * listener interface for busy loader
 */
interface BusyLoaderListener {

    /**
     * show the busy loader
     * @param isShown is loader shown
     */
    fun showLoader(isShown: Boolean)

    /**
     * show error layout
     * @param isShown is error shown
     */
    fun showError(isShown: Boolean)

    /**
     * show animation
     * @param isShown is animation shown
     */
    fun showAnimation(isShown: Boolean)

    /**
     * helper for setting busy loader config
     * @param isLoaderVisible is loader to be shown
     * @param isAnimationVisible is animation to be shown
     * @param isErrorVisible is error to be shown
     */
    fun setLoaderVisibility(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean)

    /**
     * is net available
     */
    fun isNetAvailable()

    /**
     * net status live data to be changed through net check function
     */
    var mutableNetStatusChanged: MutableLiveData<Boolean>
}