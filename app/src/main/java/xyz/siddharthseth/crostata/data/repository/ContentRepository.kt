package xyz.siddharthseth.crostata.data.repository

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import xyz.siddharthseth.crostata.data.model.retrofit.*
import xyz.siddharthseth.crostata.data.service.CrostataApiService
import java.io.File

//repository for content api
class ContentRepository(private var crostataApiService: CrostataApiService) {
    /**
     * get next posts after a timestamp
     * @param token server token
     * @param birthId subject's birthId
     * @param lastTimestamp a timestamp
     */
    fun getNextPosts(token: String, birthId: String, lastTimestamp: Long): Observable<Post.PostGlove> {
        return crostataApiService.getPosts(token, birthId, lastTimestamp)
    }

    /**
     * get next posts using a request id and after a specific post
     * @param token server token
     * @param birthId subject's birthId
     * @param lastTimestamp a timestamp
     * @param requestId request id, string
     * @param after id for last index
     */
    fun getNextPosts(token: String, birthId: String, lastTimestamp: Long, requestId: String, after: Int): Observable<Post.PostGlove> {
        return crostataApiService.getPosts(token, birthId, lastTimestamp, requestId, after)
    }

    /**
     * submit like for a postId
     * @param token server token
     * @param postId postId
     * @param birthId subject's birthId
     */
    fun submitLike(token: String, postId: String, birthId: String): Observable<LikeTotal> {
        return crostataApiService.addLike(token, postId, birthId)
    }

    /**
     *clear like for a postId
     * @param token server token
     * @param postId postId
     * @param birthId subject's birthId
     */
    fun clearLike(token: String, postId: String, birthId: String): Observable<LikeTotal> {
        return crostataApiService.deleteLike(token, postId, birthId)
    }

    /**
     * get comments on a post after a timestamp
     * @param token server token
     * @param postId postId
     * @param lastTimestamp a timestamp
     */
    fun getComments(token: String, postId: String, lastTimestamp: Long): Observable<Comment.CommentGlove> {
        return crostataApiService.getComments(token, postId, lastTimestamp)
    }

    /**
     * get comments on a post using a requestId after a specific comment
     * @param token server token
     * @param postId postId
     * @param lastTimestamp a timestamp
     * @param requestId requestId
     * @param after id for last index
     */
    fun getComments(token: String, postId: String, lastTimestamp: Long, requestId: String, after: Int): Observable<Comment.CommentGlove> {
        return crostataApiService.getComments(token, postId, lastTimestamp, requestId, after)
    }

    /**
     * get subject info using birthId
     * @param token server token
     * @param birthId subject's birthId
     */
    fun getSubjectInfo(token: String, birthId: String): Observable<Subject> {
        return crostataApiService.getSubjectInfo(token, birthId)
    }

    /**
     * get posts of a subject after a timestamp
     * @param token server token
     * @param birthId subject's birthId
     * @param lastTimestamp a timestamp
     */
    fun getSubjectPosts(token: String, creatorId: String, birthId: String, lastTimestamp: Long): Observable<Post.PostGlove> {
        return crostataApiService.getProfilePosts(token, birthId, creatorId, lastTimestamp)
    }

    /**
     * get posts of a subject using requestId after a specific post
     * @param token server token
     * @param birthId subject's birthId
     * @param lastTimestamp a timestamp
     * @param requestId requestId
     * @param after id for last index
     */
    fun getSubjectPosts(token: String, creatorId: String, birthId: String, lastTimestamp: Long, requestId: String, after: Int): Observable<Post.PostGlove> {
        return crostataApiService.getProfilePosts(token, birthId, creatorId, lastTimestamp, requestId, after)
    }

    /**
     * post a comment on a post
     * @param token server token
     * @param postId postId
     * @param birthId subject's birthId
     */
    fun submitComment(token: String, postId: String, birthId: String, comment: String): Observable<Comment> {
        return crostataApiService.addComment(token, postId, birthId, comment, false)
    }

    /**
     * post a report for some content
     * @param token server token
     * @param creatorId creator
     * @param birthId subject's birthId
     * @param contentType content type, use constants
     * @param contentId content id
     *
     */
    fun submitReport(token: String, creatorId: String, birthId: String, contentType: Int, contentId: String): Observable<Response<ResponseBody>> {
        return crostataApiService.submitReport(token, creatorId, birthId, contentType, contentId)
    }

    /**
     * get all reports made by a subject
     * @param token server token
     * @param birthId subject's birthId
     */
    fun getReports(token: String, birthId: String): Observable<List<Report>> {
        return crostataApiService.getReports(token, birthId)
    }

    /**
     * post a text post
     * @param token server token
     * @param birthId subject's birthId
     * @param postContent text content, max length 20000
     * @param isGenerated false
     */
    fun submitTextPost(token: String, birthId: String, postContent: String, isGenerated: Boolean): Observable<Response<ResponseBody>> {
        return crostataApiService.postTextPost(token, birthId, postContent, isGenerated)
    }

    /**
     * post a hybrid post
     * @param token server token
     * @param birthId subject's birthId
     * @param postContent text content, max length 20000
     * @param image file to be uploaded
     * @param isGenerated false
     */
    fun submitImagePost(token: String, birthId: String, postContent: String, image: File, isGenerated: Boolean): Observable<Response<ResponseBody>> {
        val requestBody = RequestBody.create(MediaType.parse("image/*"), image)
        val requestBody2 = RequestBody.create(MediaType.parse("text/plain"), birthId)
        val requestBody3 = RequestBody.create(MediaType.parse("text/plain"), postContent)
        val requestBody4 = RequestBody.create(MediaType.parse("text/plain"), isGenerated.toString())
        return crostataApiService.postImagePost(token, requestBody2, requestBody3, requestBody, requestBody4)
    }

    /**
     * get if server is alive
     * @param token server token
     */
    fun serverStatus(token: String): Observable<Response<ResponseBody>> {
        return crostataApiService.getServerStatus(token)
    }

    /**
     * get search result for something
     * @param token server token
     * @param searchText search text, 1<= length <= 30ish
     */
    fun getSearchResults(token: String, searchText: String): Observable<SearchResult.SearchResultGlove> {
        return crostataApiService.getSearchResult(token, searchText)
    }

    /**
     * get search results using requestId
     * @param token server token
     * @param requestId requestId
     * @param searchText search text, 1<= length <= 30ish
     * @param after id for last index
     */
    fun getSearchResults(token: String, requestId: String, searchText: String, after: Int): Observable<SearchResult.SearchResultGlove> {
        return crostataApiService.getSearchResult(token, searchText, requestId, after)
    }

    companion object {
        const val PROFILE = 0
        const val POST = 1
        const val COMMENT = 2
    }
}