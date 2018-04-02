package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView

interface ChartRecyclerViewListener{
    fun loadProfileImage(birthId: String, profileImage: ImageView,dimension:Int)
}