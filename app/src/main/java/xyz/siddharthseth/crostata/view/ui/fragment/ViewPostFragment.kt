package xyz.siddharthseth.crostata.view.ui.fragment

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
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.fragment_view_post.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.adapter.ViewPostCommentAdapter
import xyz.siddharthseth.crostata.viewmodel.fragment.ViewPostViewModel
import java.text.SimpleDateFormat
import java.util.*

class ViewPostFragment : Fragment() {
    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    private lateinit var commentSubscription: Subscription
    private lateinit var adapter: ViewPostCommentAdapter

    private lateinit var viewPostViewModel: ViewPostViewModel
    val TAG: String? = javaClass.simpleName
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            post = it.getParcelable("post")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_post, container, false)
    }

    override fun onResume() {
        super.onResume()

        viewPostViewModel = ViewModelProviders.of(this).get(ViewPostViewModel::class.java)
        viewPostViewModel.post = post

        initializeView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun openProfile(birthId: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Post) =
                ViewPostFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("post", param1)
                    }
                }
    }

    private fun initializeView() {

        nameTextView.text = viewPostViewModel.post.creatorName.toUpperCase()
        nameTextView.setOnClickListener { listener?.openProfile(post.creatorId) }

        if (viewPostViewModel.post.contentType == "TO") {
            imageView.visibility = View.GONE
            viewPostViewModel.clearPostedImageGlide(imageView)
        } else {
            imageView.visibility = View.VISIBLE

            Log.v(TAG, "imageId-" + viewPostViewModel.post.imageId)
            viewPostViewModel.loadPostedImage(imageView)
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
        commentHeader.text = if (adapter.commentList.size > 0) "Comments" else "No Comments"
    }
}
