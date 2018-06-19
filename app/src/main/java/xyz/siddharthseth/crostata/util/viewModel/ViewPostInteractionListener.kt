package xyz.siddharthseth.crostata.util.viewModel

import xyz.siddharthseth.crostata.data.model.SnackbarMessage

interface ViewPostInteractionListener {

    /**
     * open profile
     * @param birthId birthId of the profile to be opened
     * @param name name of the profile to be opened
     */
    fun openProfile(birthId: String, name: String)

    /**
     * show snackbar
     * @param snackbarMessage the message to be displayed
     */
    fun showSnackbar(snackbarMessage: SnackbarMessage)
}