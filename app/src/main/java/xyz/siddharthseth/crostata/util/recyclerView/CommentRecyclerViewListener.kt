package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView

interface CommentRecyclerViewListener {
    fun loadProfileImage(birthId: String, profileImage: ImageView)
}