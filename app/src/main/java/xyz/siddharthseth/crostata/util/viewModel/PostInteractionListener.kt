package xyz.siddharthseth.crostata.util.viewModel

import xyz.siddharthseth.crostata.data.model.Post

interface PostInteractionListener {
    fun openFullPost(post: Post)
    fun openProfile(birthId: String)
    fun addNewPost()
}