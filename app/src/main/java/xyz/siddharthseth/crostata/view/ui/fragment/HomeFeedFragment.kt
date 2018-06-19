package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.util.ViewPreloadSizeProvider
import kotlinx.android.synthetic.main.fragment_home_feed.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.viewModel.PostInteractionListener
import xyz.siddharthseth.crostata.viewmodel.fragment.HomeFeedViewModel
import java.util.*


class HomeFeedFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        if (v != null && mListener != null) {
            when (v.id) {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //restore scroll position
        if (savedInstanceState != null) {
            scrollPositionData = savedInstanceState.getParcelable("ScrollPosition")
        }

        if (arguments != null) {
        }
        homeFeedViewModel = ViewModelProviders.of(this).get(HomeFeedViewModel::class.java)
        homeFeedViewModel.glide = GlideApp.with(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("ScrollPosition", recyclerView.layoutManager.onSaveInstanceState())
    }

    override fun onStart() {
        super.onStart()
        if (!isInitialized) {
            initUi()
        }
        addObservers()
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    override fun onResume() {
        super.onResume()

        if (!isInitialized) {
            homeFeedViewModel.getPosts()
            isInitialized = true
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = if (context is PostInteractionListener) {
            context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun initUi() {
        val manager = LinearLayoutManager(context)
        manager.isItemPrefetchEnabled = true
        if (scrollPositionData != null) {
            manager.onRestoreInstanceState(scrollPositionData)
        }

        val sizeProvider = ViewPreloadSizeProvider<GlideUrl>()
        val modelProvider = MyPreloadModelProvider()
        val preLoader = RecyclerViewPreloader<GlideUrl>(GlideApp.with(activity?.applicationContext!!), modelProvider, sizeProvider, 5)

        recyclerView.addOnScrollListener(preLoader)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = manager
        recyclerView.adapter = homeFeedViewModel.postFeedAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //pagination
                if (dy > 0) {
                    checkMorePostsNeeded(recyclerView)
                }
            }
        })

        swipeRefresh.setOnRefreshListener {
            homeFeedViewModel.refreshData()
        }

    }

    //observers
    //add helper
    private fun addObservers() {
        homeFeedViewModel.mutablePost.observe(this, observer)
        homeFeedViewModel.mutableSubject.observe(this, observerSubject)
        homeFeedViewModel.mutableLoaderConfig.observe(this, observerLoaderConfig)
        mListener!!.mutableNetStatusChanged.observe(this, observerNetStatus)
    }

    //remove helper
    private fun removeObservers() {
        homeFeedViewModel.mutablePost.removeObserver(observer)
        homeFeedViewModel.mutableSubject.removeObserver(observerSubject)
        homeFeedViewModel.mutableLoaderConfig.removeObserver(observerLoaderConfig)
        mListener!!.mutableNetStatusChanged.removeObserver(observerNetStatus)
    }

    //observer for selected post
    private val observer: Observer<Post> = Observer {
        if (it != null) {
            mListener?.openFullPost(it)
        }
    }

    //observer for selected profile
    private val observerSubject: Observer<Subject> = Observer {
        if (it != null) {
            mListener?.openProfile(it.birthId, it.name)
        }
    }

    //observer for busy loader config
    private val observerLoaderConfig: Observer<List<Boolean>> = Observer {
        if (it != null) {
            if (!it[0]) {
                swipeRefresh.isRefreshing = false
            }
            mListener!!.setLoaderVisibility(it[0], it[1], it[2])
        }
    }

    //observer with net status change
    private val observerNetStatus: Observer<Boolean> = Observer {
        if (it != null) {
            if (it && homeFeedViewModel.isLoadPending) {
                mListener!!.setLoaderVisibility(true, true, false)
                homeFeedViewModel.refreshData()
            }
        }
    }

    //check if more posts are needed on scroll
    private fun checkMorePostsNeeded(recyclerView: RecyclerView) {
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (layoutManager.itemCount <=
                (layoutManager.findLastVisibleItemPosition() + toleranceEndlessScroll)) {
            homeFeedViewModel.getNextPosts()
        }
    }

    private lateinit var homeFeedViewModel: HomeFeedViewModel
    private var mListener: PostInteractionListener? = null
    private var scrollPositionData: Parcelable? = null
    private var isInitialized: Boolean = false

    companion object {
        private const val toleranceEndlessScroll = 3
        private val TAG: String = HomeFeedFragment::class.java.simpleName
        fun newInstance(): HomeFeedFragment {
            return HomeFeedFragment()
        }
    }

    private inner class MyPreloadModelProvider : PreloadModelProvider<GlideUrl> {
        override fun getPreloadItems(position: Int): MutableList<GlideUrl> {
            val post = homeFeedViewModel.postFeedAdapter.postList[position]
            val glideUrl = GlideUrl(context!!.getString(R.string.server_url) +
                    "subject/profileImage?birthId=" + post.creatorId + "&dimen=640&quality=80"
                    , LazyHeaders.Builder()
                    .addHeader("authorization", SharedPreferencesService().getToken(activity?.applicationContext!!))
                    .build())
            return if (TextUtils.isEmpty(glideUrl.toStringUrl())) {
                Collections.emptyList()
            } else Collections.singletonList(glideUrl)
        }

        override fun getPreloadRequestBuilder(url: GlideUrl): RequestBuilder<Drawable> {
            return GlideApp.with(activity?.applicationContext!!)
                    .load(url)
                    .override(640, 320)
        }
    }
}


