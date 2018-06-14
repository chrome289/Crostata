package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView
import xyz.siddharthseth.crostata.data.model.retrofit.SearchResult

interface SearchResultListener {
    fun clearImage(imageView: ImageView)
    fun openProfile(index: Int)
    fun loadProfileImage(searchResult: SearchResult, dimen: Int, imageView: ImageView)

}