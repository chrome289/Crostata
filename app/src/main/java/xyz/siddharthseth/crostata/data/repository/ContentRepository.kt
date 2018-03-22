package xyz.siddharthseth.crostata.data.repository

import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.ImageMetadata
import xyz.siddharthseth.crostata.data.model.retrofit.NextComments
import xyz.siddharthseth.crostata.data.model.retrofit.NextPosts
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class ContentRepository(private var crostataApiService: CrostataApiService) {
    private val TAG = "ContentRepository"

    fun getNextPosts(token: String, noOfPosts: Int, lastTimestamp: Float, birthId: String): Observable<NextPosts> {
        return crostataApiService.getPosts(token, noOfPosts, lastTimestamp, birthId)
    }

    fun submitVote(token: String, postId: String, birthId: String, value: Int): Observable<VoteTotal> {
        return crostataApiService.addVote(token, postId, birthId, value)
    }

    fun clearVote(token: String, postId: String, birthId: String): Observable<VoteTotal> {
        return crostataApiService.deleteVote(token, postId, birthId)
    }

    fun getImageMetadata(token: String, postId: String): Observable<ImageMetadata> {
        return crostataApiService.imageMetadata(token, postId)
    }

    fun getComments(token: String, noOfComments: Int, lastTimestamp: Float, postId: String): Observable<NextComments> {
        return crostataApiService.getComments(token, noOfComments, lastTimestamp, postId)
    }
}