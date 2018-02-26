package xyz.siddharthseth.crostata.data.repository

import rx.Observable
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.NextPosts
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class ContentRepository(private var crostataApiService: CrostataApiService) {
    val TAG = "ContentRepository"

    fun getNextPostsList(token: String, noOfPosts: Int, lastTimestamp: Float): Observable<NextPosts> {
        return crostataApiService.getNextPostsList(token, noOfPosts, lastTimestamp)
    }

    fun getTextPost(token: String, postId: String): Observable<Post> {
        return crostataApiService.getTextPost(token, postId)
    }
}