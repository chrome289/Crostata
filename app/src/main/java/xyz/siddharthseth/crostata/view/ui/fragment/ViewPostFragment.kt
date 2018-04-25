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
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.fragment_view_post.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.viewmodel.fragment.ViewPostViewModel
import java.text.SimpleDateFormat
import java.util.*

class ViewPostFragment : Fragment() {
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

    private val observer: Observer<Post> = Observer {
        Log.v(TAG, "observer called")
        if (it != null) {
            initView()
        }
    }

    private val observerBirthId: Observer<String> = Observer {
        Log.v(TAG, "birthid observer called")
        if (it != null) {
            listener?.openProfile(it)
        }
    }
    private val observerCommentCount: Observer<Int> = Observer {
        if (it != null) {
            setCommentTextView()
        }
    }

    override fun onResume() {
        super.onResume()

        viewPostViewModel = ViewModelProviders.of(this).get(ViewPostViewModel::class.java)
        viewPostViewModel.glide = GlideApp.with(this)
        viewPostViewModel.mutablePost.observe(this, observer)
        viewPostViewModel.mutableBirthId.observe(this, observerBirthId)
        viewPostViewModel.commentCount.observe(this, observerCommentCount)

        if (!isInitialized) {
            viewPostViewModel.initPost(post)
            isInitialized = true
        }
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

    override fun onStop() {
        super.onStop()

        viewPostViewModel.mutablePost.removeObserver(observer)
        viewPostViewModel.mutableBirthId.removeObserver(observerBirthId)
        viewPostViewModel.commentCount.removeObserver(observerCommentCount)
    }

    interface OnFragmentInteractionListener {
        fun openProfile(birthId: String)
    }

    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    private lateinit var viewPostViewModel: ViewPostViewModel
    private val TAG: String? = javaClass.simpleName
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var post: Post
    private var isInitialized = false

    companion object {
        @JvmStatic
        fun newInstance(param1: Post) =
                ViewPostFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("post", param1)
                    }
                }
    }

    private fun initView() {
        val post = viewPostViewModel.mutablePost.value!!
        profileName.text = post.creatorName
        profileName.setOnClickListener { listener?.openProfile(post.creatorId) }

        if (post.contentType == "TO") {
            imageView.visibility = View.GONE
            viewPostViewModel.clearPostedImageGlide(imageView)
        } else {
            imageView.visibility = View.VISIBLE

            Log.v(TAG, "imageId-" + post.imageId)
            viewPostViewModel.loadPostedImage(post, 640, imageView)
            imageView.requestLayout()
        }

        viewPostViewModel.loadProfileImage(post, 128, profileImage)

        textPost.text = post.text

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = inputFormat.parse(post.timeCreated)

        timeTextView.text = TimeAgo.using(calendar.timeInMillis).toUpperCase()

        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = viewPostViewModel.adapter

        viewPostViewModel.getComments()

        setCommentTextView()
    }

    private fun setCommentTextView() {
        commentHeader.text = if (viewPostViewModel.commentCount.value!!.toInt() > 0) "Comments" else "No Comments"
    }
}
