package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.fragment_view_post.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.adapter.ViewPostCommentAdapter
import xyz.siddharthseth.crostata.viewmodel.ViewPostViewModel
import java.text.SimpleDateFormat
import java.util.*

class ViewPostFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var viewPostViewModel: ViewPostViewModel
    private lateinit var adapter: ViewPostCommentAdapter

    val TAG = "ViewPostFragment"
    fun onButtonPressed(uri: Uri) {
        /*if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }*/
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_post, container, false)
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
        mListener?.showBottomNavigation(false)
        mListener?.showToolbarMain(false)

        viewPostViewModel = ViewModelProviders.of(this).get(ViewPostViewModel::class.java)
        viewPostViewModel.post = post

        initializeView()
    }

    private fun initializeView() {
        nameTextView.text = viewPostViewModel.post.creatorName.toUpperCase()

        if (viewPostViewModel.post.contentType == "TO") {
            imageView.visibility = View.GONE
            viewPostViewModel.clearPostedImageGlide(imageView)
        } else {
            imageView.visibility = View.VISIBLE
            viewPostViewModel.loadPostedImage(viewPostViewModel.post, imageView)
            imageView.requestLayout()
        }

        viewPostViewModel.loadProfileImage(viewPostViewModel.post.creatorId, profileImage)

        textPost.text = viewPostViewModel.post.text

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = inputFormat.parse(viewPostViewModel.post.timeCreated)

        timeTextView.text = TimeAgo.using(calendar.timeInMillis).toUpperCase()


        adapter = ViewPostCommentAdapter(viewPostViewModel)
        adapter.setHasStableIds(true)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        commentSubscription = viewPostViewModel.getComments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ comment: Comment ->
                    adapter.commentList.add(comment)
                    setCommentTextView()
                    adapter.notifyItemInserted(adapter.commentList.size - 1)
                }, { error -> error.printStackTrace() })

        setCommentTextView()
    }

    private fun setCommentTextView() {
        commentTextView.text = if (adapter.commentList.size > 0) "Comments" else "No Comments"
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
        mListener?.showBottomNavigation(true)
        mListener?.showToolbarMain(true)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun showBottomNavigation(shouldShow: Boolean)
        fun showToolbarMain(shouldShow: Boolean)
    }

    private var post: Post = Post()
    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    private lateinit var commentSubscription: Subscription

    companion object {
        fun newInstance(post: Post): ViewPostFragment {
            val fragment = ViewPostFragment()
            fragment.post = post
            return fragment
        }
    }
}