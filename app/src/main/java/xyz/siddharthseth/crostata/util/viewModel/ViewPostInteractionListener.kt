package xyz.siddharthseth.crostata.util.viewModel

interface ViewPostInteractionListener {
    fun openProfile(birthId: String)
    fun enableNavigationDrawer(isEnabled: Boolean)
    fun showBackNavigationButton(isShown: Boolean)
}