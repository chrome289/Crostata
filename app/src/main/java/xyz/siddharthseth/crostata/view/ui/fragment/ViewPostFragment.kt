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
import kotlinx.android.synthetic.main.fragment_view_post.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.util.viewModel.ViewPostInteractionListener
import xyz.siddharthseth.crostata.viewmodel.fragment.ViewPostViewModel

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

    override fun onResume() {
        super.onResume()

        viewPostViewModel = ViewModelProviders.of(this).get(ViewPostViewModel::class.java)
        viewPostViewModel.glide = GlideApp.with(this)
        viewPostViewModel.mutablePost.observe(this, observer)
        viewPostViewModel.mutableBirthId.observe(this, observerBirthId)

        if (!isInitialized) {
            viewPostViewModel.initPost(post)
            isInitialized = true
        }
        listener?.showNavBar(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewPostInteractionListener) {
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
        listener?.showNavBar(true)
    }

    private lateinit var viewPostViewModel: ViewPostViewModel
    private val TAG: String? = javaClass.simpleName
    private var listener: ViewPostInteractionListener? = null
    private lateinit var post: Post
    private var isInitialized = false

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
                ViewPostFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("post", bundle.getParcelable("post"))
                    }
                }
    }

    private fun initView() {
        val post = viewPostViewModel.mutablePost.value!!
        profileName.text = post.creatorName
        profileName.setOnClickListener { viewPostViewModel.openProfile(post.creatorId) }

        timeText.text = post.timeCreatedText

        reportButton.setOnClickListener { viewPostViewModel.onReportButtonClick(post) }

        if (post.contentType == "TO") {
            contentImage.visibility = View.GONE
            viewPostViewModel.clearPostedImageGlide(contentImage)
        } else {
            contentImage.visibility = View.VISIBLE
            viewPostViewModel.loadPostedImage(post, 640, contentImage)
        }
        viewPostViewModel.loadProfileImage(post, 128, profileImage)

        contentText.text = post.text

        commentsTotal.text = "${post.comments} comments"
        votesTotal.text = "${post.votes}"
        votesTotal.setTextColor(
                when {
                    post.opinion == 0 -> viewPostViewModel.extraDarkGrey
                    post.opinion == 1 -> viewPostViewModel.upVoteColorTint
                    else -> viewPostViewModel.downVoteColorTint
                }
        )
        upVoteButton.setOnClickListener { viewPostViewModel.handleVote(post, 1) }
        upVoteButton.imageTintList =
                (if (post.opinion == 1) viewPostViewModel.upVoteColorTint
                else viewPostViewModel.unSelectedGrey)

        downVoteButton.setOnClickListener { viewPostViewModel.handleVote(post, -1) }
        downVoteButton.imageTintList =
                (if (post.opinion == -1) viewPostViewModel.downVoteColorTint
                else viewPostViewModel.unSelectedGrey)

        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = viewPostViewModel.adapter

        commentButton.setOnClickListener {
            viewPostViewModel.onCommentButtonClick(addComment.text.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ Log.v(TAG, "the result $it") }, {
                        it.printStackTrace()
                        hideKeyboard()
                    })
        }

        viewPostViewModel.getComments()
    }

    private fun hideKeyboard() {
        Log.v(TAG, "hiding keyboard")
    }
}
