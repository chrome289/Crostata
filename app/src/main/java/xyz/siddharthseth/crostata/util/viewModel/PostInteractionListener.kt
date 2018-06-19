package xyz.siddharthseth.crostata.util.viewModel

import xyz.siddharthseth.crostata.data.model.SnackbarMessage
import xyz.siddharthseth.crostata.data.model.retrofit.Post

/**
 * listener interface for post interactions
 */
interface PostInteractionListener : BusyLoaderListener {

    /**
     * open full post
     * @param post post to be opened
     */
    fun openFullPost(post: Post)

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