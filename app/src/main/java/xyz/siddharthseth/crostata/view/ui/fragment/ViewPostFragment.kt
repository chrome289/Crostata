package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
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
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.SnackbarMessage
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
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

    private val observerSubject: Observer<Subject> = Observer {
        Log.v(TAG, "birthid observer called")
        if (it != null) {
            listener?.openProfile(it.birthId, it.name)
        }
    }

    override fun onStart() {
        super.onStart()

        viewPostViewModel = ViewModelProviders.of(this).get(ViewPostViewModel::class.java)
        viewPostViewModel.glide = GlideApp.with(this)
        viewPostViewModel.mutablePost.observe(this, observer)
        viewPostViewModel.mutableSubject.observe(this, observerSubject)

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

    override fun onStop() {
        super.onStop()

        viewPostViewModel.mutablePost.removeObserver(observer)
        viewPostViewModel.mutableSubject.removeObserver(observerSubject)
    }


    private lateinit var viewPostViewModel: ViewPostViewModel
    private val TAG: String? = javaClass.simpleName
    private var listener: ViewPostInteractionListener? = null
    private lateinit var post: Post
    private var isInitialized = false

    companion object {
        @JvmStatic
        fun newInstance(post: Post) =
                ViewPostFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("post", post)
                    }
                }
    }

    private fun initView() {
        val post = viewPostViewModel.mutablePost.value!!

        if (post.isCensored || post.isGenerated) {
            approveImage.visibility = View.VISIBLE
            approveText.visibility = View.VISIBLE
            reportButton.visibility = View.GONE
        } else {
            approveImage.visibility = View.GONE
            approveText.visibility = View.GONE
            reportButton.visibility = View.VISIBLE
            if (post.creatorId == LoggedSubject.birthId) {
                reportButton.visibility = View.GONE
            } else {
                reportButton.setOnClickListener {
                    viewPostViewModel.onReportButtonClick()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (it) {
                                    viewPostViewModel.refreshComments()
                                    setSnackbarMessage("Report submitted successfully", Snackbar.LENGTH_SHORT)
                                } else {
                                    setSnackbarMessage("Report not submitted. Try again.", Snackbar.LENGTH_SHORT)
                                }
                            }, {
                                it.printStackTrace()
                                setSnackbarMessage("Report not submitted. Try again.", Snackbar.LENGTH_SHORT)
                            })
                }
            }
        }

        profileName.text = post.creatorName
        profileName.setOnClickListener { viewPostViewModel.openProfile(post.creatorId, post.creatorName) }

        timeText.text = post.timeCreatedText.capitalize()

        if (post.contentType == "TO") {
            contentImage.visibility = View.GONE
            viewPostViewModel.clearPostedImageGlide(contentImage)
        } else {
            contentImage.visibility = View.VISIBLE
            viewPostViewModel.loadPostedImage(post, 640, contentImage)
        }
        viewPostViewModel.loadProfileImage(post, 128, profileImage)
        viewPostViewModel.loadProfileImage(128, profileImage2)

        contentText.text = post.text

        commentsTotal.text = "${post.comments} Comments"
        likesTotal.text = "${post.likes} likes"

        likeButton.setOnClickListener { viewPostViewModel.handleLike() }
        likeButton.imageTintList =
                (if (post.opinion == 1) viewPostViewModel.likeColorTint
                else viewPostViewModel.grey500)


        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = viewPostViewModel.adapter

        commentButton.setOnClickListener {
            viewPostViewModel.onCommentButtonClick(addComment.text.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it) {
                            viewPostViewModel.refreshComments()
                            setSnackbarMessage("Comment submitted successfully", Snackbar.LENGTH_SHORT)
                        } else {
                            setSnackbarMessage("Comment not submitted. Try again.", Snackbar.LENGTH_SHORT)
                        }
                    }, {
                        it.printStackTrace()
                        setSnackbarMessage("Comment not submitted. Try again.", Snackbar.LENGTH_SHORT)
                    })
        }

        viewPostViewModel.getComments()
    }

    fun setSnackbarMessage(message: String, duration: Int) {
        listener?.showSnackbar(SnackbarMessage(message, duration))
    }
}
