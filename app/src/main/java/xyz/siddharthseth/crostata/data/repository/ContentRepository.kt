package xyz.siddharthseth.crostata.data.repository

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.*
import xyz.siddharthseth.crostata.data.service.CrostataApiService
import java.io.File

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

    fun getSubjectInfo(token: String, birthId: String): Observable<Subject> {
        return crostataApiService.getSubjectInfo(token, birthId)
    }

    /*fun getSubjectComments(token: String, birthId: String, size: Int, lastTimestamp: Long): Observable<List<ProfileComment>> {
        return crostataApiService.getProfileComments(token, birthId, size, lastTimestamp)
    }*/

    fun getSubjectPosts(token: String, creatorId: String, birthId: String, size: Int, lastTimestamp: Long): Observable<List<Post>> {
        return crostataApiService.getProfilePosts(token, creatorId, birthId, size, lastTimestamp)
    }

    fun submitComment(token: String, postId: String, birthId: String, comment: String): Observable<Comment> {
        return crostataApiService.addComment(token, postId, birthId, comment, false)
    }

    fun getReports(token: String, birthId: String): Observable<List<Report>> {
        return crostataApiService.getReports(token, birthId)
    }

    fun submitTextPost(token: String, birthId: String, postContent: String, isGenerated: Boolean): Observable<Response<ResponseBody>> {
        return crostataApiService.postTextPost(token, birthId, postContent, isGenerated)
    }

    fun submitImagePost(token: String, birthId: String, postContent: String, image: File, isGenerated: Boolean): Observable<Response<ResponseBody>> {

        val requestBody = RequestBody.create(MediaType.parse("image/*"), image)
        val requestBody2 = RequestBody.create(MediaType.parse("text/plain"), birthId)
        val requestBody3 = RequestBody.create(MediaType.parse("text/plain"), postContent)
        val requestBody4 = RequestBody.create(MediaType.parse("text/plain"), isGenerated.toString())
        return crostataApiService.postImagePost(token, requestBody2, requestBody3, requestBody, requestBody4)
    }

    fun serverStatus(token: String): Observable<Response<ResponseBody>> {
        return crostataApiService.getServerStatus(token)
    }
}