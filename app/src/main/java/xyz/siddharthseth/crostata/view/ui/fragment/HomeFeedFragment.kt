package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home_feed.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.modelView.HomeFeedViewModel
import xyz.siddharthseth.crostata.view.adapter.HomeFeedAdapter

class HomeFeedFragment : Fragment(), View.OnClickListener {

    private lateinit var homeFeedViewModel: HomeFeedViewModel

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(view: View, adapterPosition: Int)
    }

    override fun onClick(v: View?) {
        if (v != null && mListener != null) {
        }
    }

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var homeFeedAdapter: HomeFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is HomeFeedFragment.OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    val TAG = "HomeFeedFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFeedViewModel = ViewModelProviders.of(this).get(HomeFeedViewModel::class.java)

        homeFeedViewModel.getNextPosts().subscribe({ postList ->
            Log.v(TAG, "got a post")
            addNewPosts(postList)
        }, { onError ->
            onError.printStackTrace()
            Log.v(TAG, "error   " + onError.stackTrace + "   " + onError.localizedMessage + "    " + onError.cause)
        })

        homeFeedAdapter = HomeFeedAdapter(homeFeedViewModel)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = homeFeedAdapter

        val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        //recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {
        fun newInstance(): HomeFeedFragment {
            return HomeFeedFragment()
        }
    }

    fun addNewPosts(postList: ArrayList<Post>) {
        homeFeedAdapter.postList.addAll(postList)
        homeFeedAdapter.sortList()
        homeFeedAdapter.notifyDataSetChanged()
    }
}
/* fun registerVote(value: Int, adapterPosition: Int) {
     val post = homeFeedAdapter.postList.get(adapterPosition)
     if (value != post.myVote) {
         post.myVote = value
     } else {
         post.myVote = 0
     }
     homeFeedAdapter.notifyItemChanged(adapterPosition, post.myVote)
 }*/

