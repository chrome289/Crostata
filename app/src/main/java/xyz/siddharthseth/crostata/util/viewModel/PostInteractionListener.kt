package xyz.siddharthseth.crostata.util.viewModel

import xyz.siddharthseth.crostata.data.model.SnackbarMessage
import xyz.siddharthseth.crostata.data.model.retrofit.Post

interface PostInteractionListener : BusyLoaderListener {
    fun openFullPost(post: Post)
    fun openProfile(birthId: String, name: String)
    fun addNewPost()

    fun showSnackbar(snackbarMessage: SnackbarMessage)
}