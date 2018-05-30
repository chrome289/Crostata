package xyz.siddharthseth.crostata.util.viewModel

import xyz.siddharthseth.crostata.data.model.retrofit.Post

interface ProfileInteractionListener : BusyLoaderListener {
    fun openProfile(birthId: String, name: String)
    fun openFullPost(post: Post)
}
