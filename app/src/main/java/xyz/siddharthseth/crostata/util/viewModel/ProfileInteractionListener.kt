package xyz.siddharthseth.crostata.util.viewModel

import xyz.siddharthseth.crostata.data.model.Post

interface ProfileInteractionListener {
    fun openProfile(birthId: String)
    fun openFullPost(post: Post)
    fun showNavBar(isShown: Boolean)
}
