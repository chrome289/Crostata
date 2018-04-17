package xyz.siddharthseth.crostata.data.repository

import rx.Observable
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.ChartEntry
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.model.retrofit.VoteTotal
import xyz.siddharthseth.crostata.data.service.CrostataApiService

class ContentRepository(private var crostataApiService: CrostataApiService) {
    private val TAG = "ContentRepository"

    fun getNextPosts(token: String, noOfPosts: Int, lastTimestamp: Long, birthId: String): Observable<List<Post>> {
        return crostataApiService.getPosts(token, birthId, noOfPosts, lastTimestamp)
    }

    fun submitVote(token: String, postId: String, birthId: String, value: Int): Observable<VoteTotal> {
        return crostataApiService.addVote(token, postId, birthId, value)
    }

    fun clearVote(token: String, postId: String, birthId: String): Observable<VoteTotal> {
        return crostataApiService.deleteVote(token, postId, birthId)
    }

    /* fun getImageMetadata(token: String, postId: String): Observable<ImageMetadata> {
         return crostataApiService.imageMetadata(token, postId)
     }*/

    fun getComments(token: String, postId: String, noOfComments: Int, lastTimestamp: Long): Observable<List<Comment>> {
        return crostataApiService.getComments(token, postId, noOfComments, lastTimestamp)
    }

    fun getPatriotChart(token: String): Observable<List<ChartEntry>> {
        return crostataApiService.getPatriotChart(token)
    }

    fun getSubjectInfo(token: String, birthId: String): Observable<Subject> {
        return crostataApiService.getSubjectInfo(token, birthId)
    }

    fun getSubjectComments(token: String, birthId: String, size: Int, lastTimestamp: Long): Observable<List<Comment>> {
        return crostataApiService.getProfileComments(token, birthId, size, lastTimestamp)
    }

    fun getSubjectPosts(token: String, birthId: String, size: Int, lastTimestamp: Long): Observable<List<Post>> {
        return crostataApiService.getProfilePosts(token, birthId, size, lastTimestamp)
    }
}