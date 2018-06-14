package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.util.ViewPreloadSizeProvider
import kotlinx.android.synthetic.main.activity_search.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.viewModel.BusyLoaderListener
import xyz.siddharthseth.crostata.viewmodel.activity.SearchActivityViewModel
import java.util.*


class SearchActivity : AppCompatActivity(), BusyLoaderListener {

    override fun showError(isShown: Boolean) {
        errorLayoutSearch.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showLoader(isShown: Boolean) {
        loadingFrame.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showAnimation(isShown: Boolean) {
        if (isShown) {
            animationViewSearch.setAnimation(R.raw.loader1)
            animationViewSearch.scale = 0.2f
            animationViewSearch.visibility = View.VISIBLE
            animationViewSearch.playAnimation()
        } else {
            animationViewSearch.cancelAnimation()
            animationViewSearch.visibility = View.GONE
        }
    }

    override fun setLoaderVisibility(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
        Log.d(TAG, "setLoaderVisibility $isLoaderVisible $isAnimationVisible $isErrorVisible")

        showLoader(isLoaderVisible)
        showAnimation(isAnimationVisible)
        showError(isErrorVisible)
/*
        if (searchActivityViewModel.isSearchResultsAvailable) {
            footer.visibility = View.VISIBLE
        } else {
            footer.visibility = View.GONE
        }*/
    }

    override fun isNetAvailable() {
        searchActivityViewModel.checkNetworkAvailable()
    }

    override var mutableNetStatusChanged: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState != null) {
            scrollPositionData = savedInstanceState.getParcelable("ScrollPosition")
        }
        animationViewSearch.setAnimation(R.raw.loader1)
        animationViewSearch.scale = 0.2f
        animationViewSearch.visibility = View.VISIBLE
        animationViewSearch.playAnimation()

        searchActivityViewModel = ViewModelProviders.of(this).get(SearchActivityViewModel::class.java)
        searchActivityViewModel.glide = GlideApp.with(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("ScrollPosition", resultRecyclerView.layoutManager.onSaveInstanceState())
    }

    override fun onStart() {
        super.onStart()
        addObservers()
        if (!isInitialized) {

            val manager = LinearLayoutManager(this)
            manager.isItemPrefetchEnabled = true
            if (scrollPositionData != null) {
                manager.onRestoreInstanceState(scrollPositionData)
            }

            val sizeProvider = ViewPreloadSizeProvider<GlideUrl>()
            val modelProvider = MyPreloadModelProvider()
            val preLoader = RecyclerViewPreloader<GlideUrl>(GlideApp.with(applicationContext), modelProvider, sizeProvider, 5)
            resultRecyclerView.addOnScrollListener(preLoader)
            resultRecyclerView.setHasFixedSize(true)
            resultRecyclerView.layoutManager = manager
            resultRecyclerView.adapter = searchActivityViewModel.searchResultAdapter
            resultRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    //pagination
                    if (dy > 0) {
                        checkMorePostsNeeded(resultRecyclerView)
                    }
                }
            })

            searchQuery.setOnEditorActionListener { view, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchQuery = view.text.toString()
                    resetSearch()
                    view.text = searchQuery
                    searchActivityViewModel.searchText = searchQuery
                    searchActivityViewModel.getSearchResults()
                    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
                return@setOnEditorActionListener true
            }
            searchClear.setOnClickListener {
                resetSearch()
            }
            refreshButton.setOnClickListener {
                isNetAvailable()
            }
            isInitialized = true
        }
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun addObservers() {
        searchActivityViewModel.mutableProfile.observe(this, observerProfile)
        searchActivityViewModel.mutableLoaderConfig.observe(this, observerLoaderConfig)
    }

    private fun removeObservers() {
        searchActivityViewModel.mutableProfile.removeObserver(observerProfile)
        searchActivityViewModel.mutableLoaderConfig.removeObserver(observerLoaderConfig)
    }

    private fun resetSearch() {
        searchQuery.text.clear()
        searchActivityViewModel.clearList()
        setLoaderVisibility(false, false, false)
    }

    private fun checkMorePostsNeeded(resultRecyclerView: RecyclerView) {
        val layoutManager: LinearLayoutManager = resultRecyclerView.layoutManager as LinearLayoutManager
        if (layoutManager.itemCount <=
                (layoutManager.findLastVisibleItemPosition() + toleranceEndlessScroll)) {
            searchActivityViewModel.getNextSearchResults()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DETAIL_ACTIVITY_RESULT_CODE -> {
                if (data != null) {
                    if (data.hasExtra("openItem")) {
                        Log.d(TAG, "i am not working 2")
                        val openItemId = data.getIntExtra("openItem", R.id.home)
                        //navigationDrawerListener(navigationView.menu.findItem(openItemId))
                    }
                }
                searchActivityViewModel.isDetailActivityOpen = false
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate()
            /* detailActivityViewModel.toolbarTitle
                     .removeAt(detailActivityViewModel.toolbarTitle.size - 1)
             val temp = detailActivityViewModel.toolbarTitle.last()
             titleTextView.text = temp*/
        } else {
            finish()
        }
    }

    lateinit var searchActivityViewModel: SearchActivityViewModel
    private var isInitialized = false
    private val TAG: String = javaClass.simpleName
    private val toleranceEndlessScroll = 3
    private var scrollPositionData: Parcelable? = null

    private val DETAIL_ACTIVITY_RESULT_CODE: Int = 0

    private var observerProfile: Observer<Subject> = Observer {
        if (it != null) {
            val intent = Intent(this, DetailActivity::class.java)
            if (LoggedSubject.birthId == it.birthId) {
                intent.putExtra("birthId", it.birthId)
                intent.putExtra("name", "My Profile")
            } else {
                intent.putExtra("birthId", it.birthId)
                intent.putExtra("name", it.name)
            }
            if (!searchActivityViewModel.isDetailActivityOpen) {
                searchActivityViewModel.isDetailActivityOpen = true
                startActivityForResult(intent, DETAIL_ACTIVITY_RESULT_CODE)
            }
        }
    }

    private val observerLoaderConfig: Observer<List<Boolean>> = Observer {
        if (it != null) {
            if (!it[0]) {
                // swipeRefresh.isRefreshing = false
            }
            setLoaderVisibility(it[0], it[1], it[2])
        }
    }

    private inner class MyPreloadModelProvider : ListPreloader.PreloadModelProvider<GlideUrl> {
        override fun getPreloadItems(position: Int): MutableList<GlideUrl> {
            val searchResult = searchActivityViewModel.searchResultAdapter.searchResultList[position]
            val glideUrl = GlideUrl(getString(R.string.server_url) +
                    "subject/profileImage?birthId=" + searchResult.birthId + "&dimen=640&quality=80"
                    , LazyHeaders.Builder()
                    .addHeader("authorization", SharedPreferencesService().getToken(applicationContext))
                    .build())
            return if (TextUtils.isEmpty(glideUrl.toStringUrl())) {
                Collections.emptyList()
            } else Collections.singletonList(glideUrl)
        }

        override fun getPreloadRequestBuilder(url: GlideUrl): RequestBuilder<Drawable> {
            return GlideApp.with(applicationContext)
                    .load(url)
                    .override(256, 256)
        }
    }
}
