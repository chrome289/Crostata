package xyz.siddharthseth.crostata.viewmodel.activity

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v7.util.DiffUtil
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.glide.GlideRequests
import xyz.siddharthseth.crostata.data.model.livedata.SingleSubject
import xyz.siddharthseth.crostata.data.model.retrofit.SearchResult
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.diffUtil.SearchResultDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener
import xyz.siddharthseth.crostata.util.recyclerView.SearchResultListener
import xyz.siddharthseth.crostata.view.adapter.SearchResultAdapter

/**
 * viewmodel for search activity
 */
class SearchActivityViewModel(application: Application) : AndroidViewModel(application), SearchResultListener, FooterListener {
    override fun reloadSecondary() {
        getNextSearchResults()
    }

    override fun openProfile(index: Int) {
        mutableProfile.value = Subject(searchResultList[index].birthId, searchResultList[index].name)
    }

    override fun clearImage(imageView: ImageView) {
        glide.clear(imageView as View)
    }

    override fun loadProfileImage(searchResult: SearchResult, dimen: Int, imageView: ImageView) {
        glide.load(searchResult.glideUrlProfileThumb)
                .priority(Priority.LOW)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(imageView)
    }

    override fun updateSearchResultList() {
        val diffUtil = DiffUtil.calculateDiff(
                SearchResultDiffUtilCallback(searchResultAdapter.searchResultList, searchResultList))
        searchResultAdapter.searchResultList.clear()
        searchResultAdapter.searchResultList = SearchResult.cloneList(searchResultList)
        after = if (searchResultList.size < 2) {
            0
        } else {
            searchResultList.size
        }
        diffUtil.dispatchUpdatesTo(searchResultAdapter)

        setLoaderLiveData(false, false, false, false)
    }

    override fun fetchSearchResults() {
        isSearchRequestSent = true
        hasNewItems = false
        contentRepository.getSearchResults(token, searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    requestId = it.requestId
                    Observable.from(it.list)
                }
                .subscribe({ onRequestNext(it) }
                        , { onRequestError(it, false) }
                        , { onRequestComplete() }
                )
    }

    override fun fetchNextSearchResults() {
        isSearchRequestSent = true
        hasNewItems = false
        contentRepository.getSearchResults(token, requestId, searchText, after)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.from(it.list)
                }
                .doOnNext {
                    setLoaderLiveData(false, false, false, true)
                }
                .doOnError { setLoaderLiveData(true, false, true, true) }
                .subscribe({ onRequestNext(it) }
                        , { onRequestError(it, true) }
                        , { onRequestComplete() }
                )

    }

    /**
     * callback for onComplete
     */
    private fun onRequestComplete() {
        isSearchResultsAvailable = true
        isSearchRequestSent = false
        if (hasNewItems) {
            updateSearchResultList()
        } else {
            hasReachedEnd = true
            searchResultAdapter.isEndVisible = true
            searchResultAdapter.notifyItemChanged(searchResultAdapter.searchResultList.size - 1)
        }
    }

    /**
     * callback for onError
     */
    private fun onRequestError(throwable: Throwable, isSecondary: Boolean) {
        throwable.printStackTrace()
        setLoaderLiveData(true, false, true, isSecondary)
        isSearchRequestSent = false
    }

    /**
     * callback for onNext
     */
    private fun onRequestNext(searchResult: SearchResult) {
        val context: Context = getApplication()
        searchResult.initExtraInfo(context.getString(R.string.server_url), token)
        hasNewItems = true
        searchResultList.add(if (searchResultList.size == 0) 0 else searchResultList.size - 1, searchResult)
    }

    /**
     * clear and reset everything
     */
    fun clearList() {
        searchText = ""
        isLoadPending = false
        isSearchRequestSent = false
        isSearchResultsAvailable = false
        isServerStatusRequestSent = false
        hasNewItems = false
        hasReachedEnd = false
        searchResultList.clear()
        searchResultList.add(SearchResult())
        updateSearchResultList()
    }

    /**
     * helper for getting initial results
     */
    fun getSearchResults() {
        setLoaderLiveData(true, true, false, false)
        if (isSearchResultsAvailable) {
            updateSearchResultList()
        } else {
            if (!isSearchRequestSent) {
                fetchSearchResults()
            }
        }
    }

    /**
     * helper for getting next results
     */
    fun getNextSearchResults() {
        if (!isSearchRequestSent && !hasReachedEnd) {
            setLoaderLiveData(true, true, false, true)
            fetchNextSearchResults()
        }
    }

    /**
     * set busy loader config
     * @param isLoaderVisible is loader layout visible
     * @param isAnimationVisible is animation visible
     * @param isErrorVisible is error visible
     * @param setSecondary is secondary loader visible
     */
    private fun setLoaderLiveData(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean, setSecondary: Boolean) {
        isLoadPending = isLoaderVisible

        val tempList = ArrayList<Boolean>()
        tempList.add(isLoaderVisible)
        tempList.add(isAnimationVisible)
        tempList.add(isErrorVisible)
        if (!setSecondary) {
            mutableLoaderConfig.value = tempList
        } else {
            searchResultAdapter.isErrorVisible = isErrorVisible
            searchResultAdapter.isSecondLoaderVisible = isAnimationVisible
            searchResultAdapter.notifyItemChanged(searchResultList.size - 1)
        }
    }

    /**
     * check if internet is available
     */
    fun checkNetworkAvailable() {
        if (!isServerStatusRequestSent) {
            isServerStatusRequestSent = true
            contentRepository.serverStatus(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        isServerStatusRequestSent = false
                        setLoaderLiveData(false, false, false, false)
                        getSearchResults()
                    }, {
                        isServerStatusRequestSent = false
                        setLoaderLiveData(true, false, true, false)
                        it.printStackTrace()
                    })
        }
    }


    private val contentRepository = ContentRepositoryProvider.getContentRepository()
    private val sharedPreferencesService = SharedPreferencesService()
    private val token = sharedPreferencesService.getToken(application)
    lateinit var glide: GlideRequests
    var searchResultAdapter: SearchResultAdapter = SearchResultAdapter(this, this)

    //static stuff
    companion object {
        var TAG: String = this::class.java.simpleName
        private var searchResultList = ArrayList<SearchResult>()
        var mutableLoaderConfig = MutableLiveData<List<Boolean>>()
        var mutableProfile = SingleSubject()

        var searchText: String = ""
        private var requestId: String = ""
        private var after: Int = -1

        private var isLoadPending = false
        private var isSearchRequestSent = false
        private var isServerStatusRequestSent = false
        private var isSearchResultsAvailable = false
        private var hasReachedEnd = false
        private var hasNewItems = false
        var isDetailActivityOpen: Boolean = false
    }

    init {
        searchResultAdapter.setHasStableIds(true)
        searchResultList.add(SearchResult())
    }
}