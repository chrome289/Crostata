package xyz.siddharthseth.crostata.data.repository

import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.ImageMetadata
import xyz.siddharthseth.crostata.data.model.retrofit.NextPosts
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class ContentRepository(private var crostataApiService: CrostataApiService) {
    private val TAG = "ContentRepository"

    fun getNextPosts(token: String, noOfPosts: Int, lastTimestamp: Float, birthId: String): Observable<NextPosts> {
        return crostataApiService.nextPosts(token, noOfPosts, lastTimestamp, birthId)
    }

    fun submitVote(token: String, postId: String, birthId: String, value: Int): Observable<VoteTotal> {
        return crostataApiService.submitVote(token, postId, birthId, value)
    }

    fun clearVote(token: String, postId: String, birthId: String): Observable<VoteTotal> {
        return crostataApiService.clearVote(token, postId, birthId)
    }

    fun getImageMetadata(token: String, postId: String): Observable<ImageMetadata> {
        return crostataApiService.imageMetadata(token, postId)
    }
}