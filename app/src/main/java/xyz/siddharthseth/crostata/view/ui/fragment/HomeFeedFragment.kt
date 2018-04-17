package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home_feed.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.adapter.HomeFeedAdapter
import xyz.siddharthseth.crostata.viewmodel.fragment.HomeFeedViewModel

class HomeFeedFragment : Fragment(), View.OnClickListener {

    interface OnFragmentInteractionListener {
        fun openFullPost(post: Post)
        fun openProfile(birthId: String)
        fun addNewPost()
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
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
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

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onstop")
        postSubscription.unsubscribe()
        homeFeedViewModel.mutablePost.removeObserver(observer)
        homeFeedViewModel.mutableBirthId.removeObserver(observerBirthId)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.v(TAG, "onattach")
        mListener = if (context is HomeFeedFragment.OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
        if (!isInitialized) {
            homeFeedViewModel = ViewModelProviders.of(this).get(HomeFeedViewModel::class.java)

            homeFeedAdapter = HomeFeedAdapter(homeFeedViewModel)
            homeFeedAdapter.setHasStableIds(true)

            postSubscription = homeFeedViewModel.getPosts()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ post ->
                        homeFeedAdapter.postList.add(post)
                        homeFeedAdapter.notifyItemInserted(homeFeedAdapter.postList.size - 1)
                    }, { onError ->
                        onError.printStackTrace()
                    })

            isInitialized = true
        }

        homeFeedViewModel.mutablePost.observe(this, observer)
        homeFeedViewModel.mutableBirthId.observe(this, observerBirthId)

        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = homeFeedAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //pagination
                checkMorePostsNeeded(recyclerView, dy)

                /* //hide/show bottom bar
                 if (dy > 0)
                     mListener?.bottomNavigationVisible(false)
                 else if (dy < 0)
                     mListener?.bottomNavigationVisible(true)*/
            }
        })

        //addPostButton.setOnClickListener { v: View -> mListener?.addNewPost() }

    }

    private fun checkMorePostsNeeded(recyclerView: RecyclerView, dy: Int) {
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (!homeFeedViewModel.isLoading && dy > 0
                && layoutManager.itemCount <=
                (layoutManager.findLastVisibleItemPosition() + toleranceEndlessScroll)) {
            homeFeedViewModel.isLoading = true
            homeFeedViewModel.getNextPosts().observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ post ->
                        homeFeedAdapter.postList.add(post)
                        homeFeedAdapter.notifyItemInserted(homeFeedAdapter.postList.size - 1)
                    }, { onError ->
                        onError.printStackTrace()
                    })
        }
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

    private lateinit var postSubscription: Subscription
    private lateinit var homeFeedViewModel: HomeFeedViewModel
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var homeFeedAdapter: HomeFeedAdapter
    private var isInitialized: Boolean = false
    private val toleranceEndlessScroll = 3

    companion object {
        private val TAG: String = HomeFeedFragment::class.java.simpleName
        fun newInstance(): HomeFeedFragment {
            Log.v(TAG, "new fragment requested")
            return HomeFeedFragment()
        }
    }
}

