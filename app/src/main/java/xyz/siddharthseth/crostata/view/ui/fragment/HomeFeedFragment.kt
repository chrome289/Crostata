package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home_feed.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.modelView.HomeFeedViewModel
import xyz.siddharthseth.crostata.view.adapter.HomeFeedAdapter

class HomeFeedFragment : Fragment(), View.OnClickListener {

    interface OnFragmentInteractionListener {
        fun openFullPost(post: Post)
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


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.v(TAG, "attach")
        mListener = if (context is HomeFeedFragment.OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }


    private val TAG = "HomeFeedFragment"
    lateinit var postSubscription: Subscription

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")

        homeFeedViewModel = ViewModelProviders.of(this).get(HomeFeedViewModel::class.java)

        homeFeedAdapter = HomeFeedAdapter(homeFeedViewModel)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = homeFeedAdapter

        //val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        //recyclerView.addItemDecoration(dividerItemDecoration)


        postSubscription = homeFeedViewModel.getPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ post ->
                    Log.v(TAG, "got posts " + post)
                    homeFeedAdapter.postList.add(post)
                }, { onError ->
                    onError.printStackTrace()
                    Log.v(TAG, "error   " + onError.stackTrace + "   " + onError.localizedMessage + "    " + onError.cause)
                }, {
                    homeFeedAdapter.notifyDataSetChanged()
                })
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onpause")

        postSubscription.unsubscribe()
    }

    val observer: Observer<Post> = Observer {
        Log.v(TAG, "observer called")
        if (it != null) {
            mListener?.openFullPost(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        Log.v(TAG, "detached")
        mListener = null
        // homeFeedViewModel.resetLastTimeStamp()
    }

    companion object {
        fun newInstance(): HomeFeedFragment {
            return HomeFeedFragment()
        }
    }

    private lateinit var homeFeedViewModel: HomeFeedViewModel
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var homeFeedAdapter: HomeFeedAdapter
}

