package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.util.ViewPreloadSizeProvider
import kotlinx.android.synthetic.main.fragment_profile.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.SnackbarMessage
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.viewModel.ProfileInteractionListener
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment(), NestedScrollView.OnScrollChangeListener {

    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val dy = scrollY - oldScrollY
        //pagination
        if (dy > 0) {
            checkMorePostsNeeded(profilePostRecyclerView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            birthId = arguments!!.getString("birthId")
        }

        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        profileViewModel.glide = GlideApp.with(this)
        profileViewModel.birthId = birthId

        addObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (!isInitialized) {
            val sizeProvider = ViewPreloadSizeProvider<GlideUrl>()
            val modelProvider = MyPreloadModelProvider()
            val preLoader = RecyclerViewPreloader<GlideUrl>(GlideApp.with(activity?.applicationContext!!), modelProvider, sizeProvider, 5)
            profilePostRecyclerView.addOnScrollListener(preLoader)
            profilePostRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            profilePostRecyclerView.adapter = profileViewModel.profilePostAdapter

            nestedScrollView.setOnScrollChangeListener(this)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isInitialized) {
            mListener!!.setLoaderVisibility(true, true, false)
            profileViewModel.getInfo()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ subject: Subject ->
                        initUi(subject)
                        mListener!!.setLoaderVisibility(false, false, false)
                    }, { err ->
                        err.printStackTrace()
                        mListener!!.setLoaderVisibility(true, false, true)
                    })
            profileViewModel.getPosts()
            isInitialized = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProfileInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    //init view
    private fun initUi(subject: Subject) {
        profileViewModel.loadOwnProfileImage(640, profileImage)

        profileName.text = subject.name.capitalize()

        rank.text = getString(R.string.ranked, subject.rank.toString())
        postTotal.text = subject.posts.toString()
        commentTotal.text = subject.comments.toString()

        patriotIndex.text = subject.patriotIndex.toString()

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val date = inputFormat.parse(subject.dob)
        dateOfBirth.text = getString(R.string.profile_dob, outputFormat.format(date))

        gender.text = if (subject.gender == 0) "Male" else "Female"
        profession.text = subject.profession.toLowerCase().capitalize()

        if (subject.birthId == LoggedSubject.birthId) {
            reportButton.visibility = View.GONE
            seperator.visibility = View.INVISIBLE
        } else {
            reportButton.visibility = View.VISIBLE
            seperator.visibility = View.VISIBLE
            reportButton.setOnClickListener {
                profileViewModel.onReportButtonClick()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            setSnackbarMessage("Profile reported successfully", Snackbar.LENGTH_SHORT)
                        }, {
                            setSnackbarMessage("Profile could not be reported", Snackbar.LENGTH_SHORT)
                        })
            }
        }
    }

    //set snackbar message
    private fun setSnackbarMessage(message: String, length: Int) {
        mListener?.showSnackbar(SnackbarMessage(message, length))
    }

    //check if more posts are needed on scroll
    private fun checkMorePostsNeeded(recyclerView: RecyclerView) {
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (layoutManager.itemCount <=
                (layoutManager.findLastVisibleItemPosition() + toleranceEndlessScroll)) {
            profileViewModel.getMorePosts()
        }
    }

    //observers
    //add helper
    private fun addObservers() {
        profileViewModel.mutablePost.observe(this, observer)
        profileViewModel.mutableLoaderConfig.observe(this, observerLoaderConfig)
        mListener!!.mutableNetStatusChanged.observe(this, observerNetStatus)
    }

    //remove observers
    private fun removeObservers() {
        profileViewModel.mutablePost.removeObserver(observer)
        profileViewModel.mutableLoaderConfig.removeObserver(observerLoaderConfig)
        mListener!!.mutableNetStatusChanged.removeObserver(observerNetStatus)
    }

    //observer for selected post
    private val observer: Observer<Post> = Observer {
        if (it != null) {
            mListener?.openFullPost(it)
        }
    }

    //observer for busy loader config
    private val observerLoaderConfig: Observer<List<Boolean>> = Observer {
        if (it != null) {
            mListener!!.setLoaderVisibility(it[0], it[1], it[2])
        }
    }

    //observer for net status change
    private val observerNetStatus: Observer<Boolean> = Observer {
        if (it != null) {
            if (it && profileViewModel.isLoadPending) {
                mListener!!.setLoaderVisibility(true, true, false)
                profileViewModel.refreshData()
            }
        }
    }

    private var mListener: ProfileInteractionListener? = null
    private lateinit var profileViewModel: ProfileViewModel
    private var isInitialized = false

    companion object {
        private const val toleranceEndlessScroll = 2
        val TAG: String = this::class.java.simpleName
        var birthId: String = ""
        @JvmStatic
        fun newInstance(birthId: String) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        this.putString("birthId", birthId)
                    }
                }
    }


    private inner class MyPreloadModelProvider : ListPreloader.PreloadModelProvider<GlideUrl> {
        override fun getPreloadItems(position: Int): MutableList<GlideUrl> {
            val post = profileViewModel.profilePostAdapter.postList[position]
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
        }
    }
}
