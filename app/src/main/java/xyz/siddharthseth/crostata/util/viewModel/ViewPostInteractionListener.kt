package xyz.siddharthseth.crostata.util.viewModel

import xyz.siddharthseth.crostata.data.model.SnackbarMessage

interface ViewPostInteractionListener {
    fun openProfile(birthId: String, name: String)

    fun showSnackbar(snackbarMessage: SnackbarMessage)
}