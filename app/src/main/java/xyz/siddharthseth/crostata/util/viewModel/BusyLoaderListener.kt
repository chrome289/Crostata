package xyz.siddharthseth.crostata.util.viewModel

interface BusyLoaderListener {
    fun showLoader(isShown: Boolean)
    fun showError(isShown: Boolean)
    fun showAnimation(isShown: Boolean)
}