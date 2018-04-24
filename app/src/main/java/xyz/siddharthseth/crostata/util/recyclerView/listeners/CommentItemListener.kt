package xyz.siddharthseth.crostata.util.recyclerView.listeners

import android.widget.ImageView

interface CommentItemListener {
    fun loadProfileImage(birthId: String, profileImage: ImageView)
}