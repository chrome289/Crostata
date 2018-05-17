package xyz.siddharthseth.crostata.util.viewModel

interface ViewPostInteractionListener {
    fun openProfile(birthId: String)
    fun showNavBar(isShown: Boolean)
    fun showBackButton(isShown: Boolean)
}