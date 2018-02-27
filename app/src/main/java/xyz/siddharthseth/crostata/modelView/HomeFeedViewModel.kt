package xyz.siddharthseth.crostata.modelView

import android.content.Context
import android.util.Log
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService

class HomeFeedViewModel {

    private val TAG = "LoginActivityViewModel"
    private val sharedPrefrencesService = SharedPrefrencesService()
    val contentRepository = ContentRepositoryProvider.getContentRepository()

    var token: String = ""
    var noOfPosts: Int = 10
    var lastTimestamp: Float = 1519334692.544f

    fun getNextPosts(context: Context): Observable<ArrayList<Post>> {
        token = sharedPrefrencesService.getToken(context)
        return contentRepository.getNextPostsList(token, noOfPosts, lastTimestamp)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .concatMap({ nextPosts ->
                    Log.v(TAG, "postID    " + nextPosts.posts.size)
                    return@concatMap Observable.just(nextPosts.posts)
                })
        /*.flatMap({ post: Post ->
                    Log.v(TAG, "postID    " + post.postId)
                    Observable.just(post)
                })*/
    }
/*
    fun getImagePost(postId: String): Observable<Post> {
        return Observable.just(Post())
    }

    fun getComboPost(postId: String): Observable<Post> {
        return Observable.just(Post())
    }*/
}
