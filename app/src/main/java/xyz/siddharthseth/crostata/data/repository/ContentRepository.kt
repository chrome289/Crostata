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

    fun getNextPosts(token: String, birthId: String, lastTimestamp: Long): Observable<Post.PostGlove> {
        return crostataApiService.getPosts(token, birthId, lastTimestamp)
    }

    fun getNextPosts(token: String, birthId: String, lastTimestamp: Long, requestId: String, after: Int): Observable<Post.PostGlove> {
        return crostataApiService.getPosts(token, birthId, lastTimestamp, requestId, after)
    }

    fun submitLike(token: String, postId: String, birthId: String, value: Int): Observable<LikeTotal> {
        return crostataApiService.addLike(token, postId, birthId, value)
    }

    fun clearLike(token: String, postId: String, birthId: String): Observable<LikeTotal> {
        return crostataApiService.deleteLike(token, postId, birthId)
    }

    fun getComments(token: String, postId: String, lastTimestamp: Long): Observable<Comment.CommentGlove> {
        return crostataApiService.getComments(token, postId, lastTimestamp)
    }

    fun getComments(token: String, postId: String, lastTimestamp: Long, requestId: String, after: Int): Observable<Comment.CommentGlove> {
        return crostataApiService.getComments(token, postId, lastTimestamp, requestId, after)
    }

    fun getSubjectInfo(token: String, birthId: String): Observable<Subject> {
        return crostataApiService.getSubjectInfo(token, birthId)
    }

    fun getSubjectPosts(token: String, creatorId: String, birthId: String, lastTimestamp: Long): Observable<Post.PostGlove> {
        return crostataApiService.getProfilePosts(token, birthId, creatorId, lastTimestamp)
    }

    fun getSubjectPosts(token: String, creatorId: String, birthId: String, lastTimestamp: Long, requestId: String, after: Int): Observable<Post.PostGlove> {
        return crostataApiService.getProfilePosts(token, birthId, creatorId, lastTimestamp, requestId, after)
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

    fun getSearchResults(token: String, searchText: String): Observable<SearchResult.SearchResultGlove> {
        return crostataApiService.getSearchResult(token, searchText)
    }


    fun getSearchResults(token: String, requestId: String, searchText: String, after: Int): Observable<SearchResult.SearchResultGlove> {
        return crostataApiService.getSearchResult(token, searchText, requestId, after)
    }

}