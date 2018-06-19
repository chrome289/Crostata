package xyz.siddharthseth.crostata.util.recyclerView

import android.widget.ImageView
import xyz.siddharthseth.crostata.data.model.retrofit.SearchResult

/**
 * listener interface for search results
 */
interface SearchResultListener {

    /**
     * clear profile image
     * @param imageView view id for imageview
     */
    fun clearImage(imageView: ImageView)

    /**
     * open profile
     * @param index index of search result
     */
    fun openProfile(index: Int)

    /**
     * load profile image
     * @param searchResult search result for profile
     * @param dimen resolution intended
     * @param imageView view id for imageview
     */
    fun loadProfileImage(searchResult: SearchResult, dimen: Int, imageView: ImageView)

    /**
     * update search result list
     * 1.calculate diff
     * 2.clear old list
     * 3.clone new to old list
     * 4.dispatch update
     */
    fun updateSearchResultList()

    /**
     * fetch initial search results
     * size = 10
     */
    fun fetchSearchResults()

    /**
     * fetch next search results. size = 10
     */
    fun fetchNextSearchResults()
}