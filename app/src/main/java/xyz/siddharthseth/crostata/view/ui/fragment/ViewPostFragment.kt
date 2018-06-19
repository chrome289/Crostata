package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_post.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.SnackbarMessage
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Comment
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Report
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.util.viewModel.ViewPostInteractionListener
import xyz.siddharthseth.crostata.viewmodel.fragment.ViewPostViewModel

class ViewPostFragment : Fragment(), View.OnClickListener, NestedScrollView.OnScrollChangeListener {

    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val dy = scrollY - oldScrollY
        //pagination
        if (dy > 0) {
            checkMoreCommentsNeeded(recyclerView)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profileName -> {
                viewPostViewModel.openProfile(post.creatorId, post.creatorName)
            }
            R.id.commentButton -> {
                viewPostViewModel.onCommentButtonClick(addComment.text.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it) {
                                viewPostViewModel.refreshComments()
                                setSnackbarMessage(Comment.MESSAGE_SUBMITTED, Snackbar.LENGTH_SHORT)
                            } else {
                                setSnackbarMessage(Comment.MESSAGE_NOT_SUBMITTED, Snackbar.LENGTH_SHORT)
                            }
                        }) {
                            it.printStackTrace()
                            setSnackbarMessage(Comment.MESSAGE_NOT_SUBMITTED, Snackbar.LENGTH_SHORT)
                        }
            }
            R.id.likeButton -> {
                viewPostViewModel.handleLike()
            }
            R.id.reportButton -> {
                viewPostViewModel.onReportButtonClick()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it) {
                                viewPostViewModel.refreshComments()
                                setSnackbarMessage(Report.MESSAGE_SUBMITTED, Snackbar.LENGTH_SHORT)
                            } else {
                                setSnackbarMessage(Report.MESSAGE_NOT_SUBMITTED, Snackbar.LENGTH_SHORT)
                            }
                        }, {
                            it.printStackTrace()
                            setSnackbarMessage(Report.MESSAGE_NOT_SUBMITTED, Snackbar.LENGTH_SHORT)
                        })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //restore scroll position
        if (savedInstanceState != null) {
            scrollPositionData = savedInstanceState.getParcelable("ScrollPosition")
        }

        arguments?.let {
            post = it.getParcelable("post")
        }
        viewPostViewModel = ViewModelProviders.of(this).get(ViewPostViewModel::class.java)
        viewPostViewModel.glide = GlideApp.with(this)

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("ScrollPosition", recyclerView.layoutManager.onSaveInstanceState())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_post, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (!isInitialized) {
            val manager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            if (scrollPositionData != null) {
                manager.onRestoreInstanceState(scrollPositionData)
            }
            recyclerView.layoutManager = manager
            recyclerView.adapter = viewPostViewModel.adapter

            //click listeners
            nestedScrollView2.setOnScrollChangeListener(this)
            profileName.setOnClickListener(this)
            reportButton.setOnClickListener(this)
            commentButton.setOnClickListener(this)
            likeButton.setOnClickListener(this)
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
            viewPostViewModel.initPost(post)
            isInitialized = true
        }
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

    //init view
    private fun initView() {
        val post = viewPostViewModel.mutablePost.value!!

        if (post.isCensored || post.isGenerated) {
            approveImage.visibility = View.VISIBLE
            approveText.visibility = View.VISIBLE
            reportButton.visibility = View.GONE
        } else {
            approveImage.visibility = View.GONE
            approveText.visibility = View.GONE
            if (post.creatorId == LoggedSubject.birthId) {
                reportButton.visibility = View.GONE
            } else {
                reportButton.visibility = View.VISIBLE
            }
        }

        profileName.text = post.creatorName

        timeText.text = post.timeCreatedText.capitalize()

        if (post.contentType == "TO") {
            contentImage.visibility = View.GONE
            viewPostViewModel.clearImageGlide(contentImage)
        } else {
            contentImage.visibility = View.VISIBLE
            viewPostViewModel.loadPostedImage(post, 1080, contentImage)
        }
        viewPostViewModel.loadProfileImage(post, 128, profileImage)
        viewPostViewModel.loadProfileImage(128, profileImage2)

        contentText.text = post.text
        commentsTotal.text = String.format(getString(R.string.comment_total, post.comments))

        likesTotal.text = String.format(getString(R.string.post_total, post.likes))
        likeButton.imageTintList =
                (if (post.opinion == 1) viewPostViewModel.likeColorTint
                else viewPostViewModel.grey500)

        viewPostViewModel.getComments()
    }

    //check if more comments are needed on scroll
    private fun checkMoreCommentsNeeded(recyclerView: RecyclerView) {
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (layoutManager.itemCount <=
                (layoutManager.findLastVisibleItemPosition() + toleranceEndlessScroll)) {
            viewPostViewModel.getNextComments()
        }
    }

    //set snackbar message
    private fun setSnackbarMessage(message: String, duration: Int) {
        listener?.showSnackbar(SnackbarMessage(message, duration))
    }

    //observers
    //add helper
    private fun addObservers() {
        viewPostViewModel.mutablePost.observe(this, observer)
        viewPostViewModel.mutableSubject.observe(this, observerSubject)
    }

    //remove observers
    private fun removeObservers() {
        viewPostViewModel.mutablePost.removeObserver(observer)
        viewPostViewModel.mutableSubject.removeObserver(observerSubject)
    }

    //observer for selected post
    private val observer: Observer<Post> = Observer {
        if (it != null) {
            initView()
        }
    }

    //observer for selected subject
    private val observerSubject: Observer<Subject> = Observer {
        if (it != null) {
            listener?.openProfile(it.birthId, it.name)
        }
    }

    private lateinit var viewPostViewModel: ViewPostViewModel
    private var listener: ViewPostInteractionListener? = null
    private var scrollPositionData: Parcelable? = null
    private var isInitialized = false

    companion object {
        private val TAG: String = this::class.java.simpleName
        private const val toleranceEndlessScroll = 3
        private lateinit var post: Post
        @JvmStatic
        fun newInstance(post: Post) =
                ViewPostFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("post", post)
                    }
                }
    }
}
