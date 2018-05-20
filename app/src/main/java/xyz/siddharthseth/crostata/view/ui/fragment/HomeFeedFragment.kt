package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
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
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.viewModel.BusyLoaderListener
import xyz.siddharthseth.crostata.util.viewModel.PostInteractionListener
import xyz.siddharthseth.crostata.viewmodel.fragment.HomeFeedViewModel
import java.util.*


class HomeFeedFragment : Fragment(), View.OnClickListener, BusyLoaderListener {
    override fun showError(isShown: Boolean) {
        if (isShown) {
            errorLayout.visibility = View.VISIBLE
        } else {
            errorLayout.visibility = View.GONE
        }
    }

    override fun showLoader(isShown: Boolean) {
        if (isShown) {
            loadingFrame.visibility = View.VISIBLE
        } else {
            loadingFrame.visibility = View.GONE
        }
    }

    override fun showAnimation(isShown: Boolean) {
        if (isShown) {
            animationView.setAnimation(R.raw.loader1)
            animationView.scale = 0.2f
            animationView.visibility = View.VISIBLE
            animationView.playAnimation()
        } else {
            animationView.cancelAnimation()
            animationView.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        if (v != null && mListener != null) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        if (arguments != null) {
        }

        homeFeedViewModel = ViewModelProviders.of(this).get(HomeFeedViewModel::class.java)
        homeFeedViewModel.glide = GlideApp.with(this)
        // homeFeedViewModel.width = DeviceUtils.getScreenWidth(this.context!!)
        homeFeedViewModel.mutablePost.observe(this, observer)
        homeFeedViewModel.mutableBirthId.observe(this, observerBirthId)
        homeFeedViewModel.mutableShowAnimation.observe(this, observerShowAnimation)
        homeFeedViewModel.mutableShowError.observe(this, observerShowError)
        homeFeedViewModel.mutableShowLoader.observe(this, observerShowLoader)
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onstop")
        homeFeedViewModel.mutablePost.removeObserver(observer)
        homeFeedViewModel.mutableBirthId.removeObserver(observerBirthId)
        homeFeedViewModel.mutableShowAnimation.removeObserver(observerShowAnimation)
        homeFeedViewModel.mutableShowError.removeObserver(observerShowError)
        homeFeedViewModel.mutableShowLoader.removeObserver(observerShowLoader)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.v(TAG, "onattach")
        mListener = if (context is PostInteractionListener) {
            context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")

        if (!isInitialized) {
            val manager = LinearLayoutManager(context)
            manager.isItemPrefetchEnabled = true

            val sizeProvider = ViewPreloadSizeProvider<GlideUrl>()
            val modelProvider = MyPreloadModelProvider()
            val preLoader = RecyclerViewPreloader<GlideUrl>(GlideApp.with(activity?.applicationContext!!), modelProvider, sizeProvider, 5)
            recyclerView.addOnScrollListener(preLoader)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = manager
            recyclerView.adapter = homeFeedViewModel.homeFeedAdapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    //pagination
                    if (dy > 0) {
                        checkMorePostsNeeded(recyclerView)
                    }
                }
            })

            homeFeedViewModel.getPosts()
            isInitialized = true
        }
        //addPostButton.setOnClickListener { v: View -> mListener?.addNewPost() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        Log.v(TAG, "ondetach")
        mListener = null
    }

    private val observer: Observer<Post> = Observer {
        Log.v(TAG, "observer called")
        if (it != null) {
            Log.v(TAG, "fragment click listener")
            mListener?.openFullPost(it)
        }
    }

    private val observerBirthId: Observer<String> = Observer {
        Log.v(TAG, "birthid observer called")
        if (it != null) {
            mListener?.openProfile(it)
        }
    }

    private val observerShowError: Observer<Boolean> = Observer {
        if (it != null) {
            showError(it)
        }
    }


    private val observerShowAnimation: Observer<Boolean> = Observer {
        if (it != null) {
            showAnimation(it)
        }
    }

    private val observerShowLoader: Observer<Boolean> = Observer {
        if (it != null) {
            showLoader(it)
        }
    }

    private fun checkMorePostsNeeded(recyclerView: RecyclerView) {
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (!homeFeedViewModel.isLoading &&
                layoutManager.itemCount <=
                (layoutManager.findLastVisibleItemPosition() + toleranceEndlessScroll)) {
            homeFeedViewModel.isLoading = true
            homeFeedViewModel.getNextPosts()
        }
    }

    private lateinit var homeFeedViewModel: HomeFeedViewModel
    private var mListener: PostInteractionListener? = null
    private var isInitialized: Boolean = false
    private val toleranceEndlessScroll = 3

    companion object {
        private val TAG: String = HomeFeedFragment::class.java.simpleName
        fun newInstance(): HomeFeedFragment {
            Log.v(TAG, "new fragment requested")
            return HomeFeedFragment()
        }
    }

    private inner class MyPreloadModelProvider : PreloadModelProvider<GlideUrl> {
        override fun getPreloadItems(position: Int): MutableList<GlideUrl> {
            val post = homeFeedViewModel.homeFeedAdapter.postList[position]
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


