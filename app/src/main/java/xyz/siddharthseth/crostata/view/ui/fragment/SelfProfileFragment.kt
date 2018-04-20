package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_self_profile.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.view.adapter.ProfileCommentAdapter
import xyz.siddharthseth.crostata.view.adapter.ProfilePostAdapter
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

class SelfProfileFragment : Fragment() {
    lateinit var birthId: String
    val TAG: String = javaClass.simpleName
    private var mListener: OnSelfProfileFragmentInteractionListener? = null
    private lateinit var profileViewModel: ProfileViewModel
    private var isInitialized = false

    private lateinit var profileCommentAdapter: ProfileCommentAdapter
    private lateinit var profilePostAdapter: ProfilePostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            birthId = arguments!!.getString("birthId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_self_profile, container, false)
    }

    private val observer: Observer<Post> = Observer {
        Log.v(TAG, "observer called")
        if (it != null) {
            Log.v(TAG, "fragment click mListener")
            mListener?.openFullPost(it)
        }
    }

    /*private val observerBirthId: Observer<String> = Observer {
        Log.v(TAG, "birthid observer called")
        if (it != null) {
            mListener?.openProfile(it)
        }
    }*/

    override fun onStop() {
        super.onStop()

        profileViewModel.mutablePost.removeObserver(observer)
    }

    override fun onResume() {
        super.onResume()

        if (!isInitialized) {
            profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
            profileViewModel.birthId = birthId
            Log.v(TAG, "loading birthid " + profileViewModel.birthId)

            isInitialized = true
        }

        profileViewModel.mutablePost.observe(this, observer)

        profileCommentAdapter = ProfileCommentAdapter(profileViewModel)
        profileCommentAdapter.setHasStableIds(true)
        profileCommentRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        profileCommentRecyclerView.adapter = profileCommentAdapter

        profilePostAdapter = ProfilePostAdapter(profileViewModel)
        profilePostAdapter.setHasStableIds(true)
        profilePostRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        profilePostRecyclerView.adapter = profilePostAdapter

        profileViewModel.getInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ subject: Subject ->
                    profileViewModel.loadProfileImage(LoggedSubject.birthId, 256, true, profileImage)

                    profileName.text = subject.name.capitalize()

                    rank.text = subject.rank.toString()
                    postTotal.text = subject.posts.toString()
                    commentTotal.text = subject.comments.toString()

                    patriotIndex.text = getString(R.string.profile_patriot_index, subject.patriotIndex)

                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
                    val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                    val date = inputFormat.parse(subject.dob)

                    dateOfBirth.text = getString(R.string.profile_dob, outputFormat.format(date))
                    gender.text = if (subject.gender == 0) "Male" else "Female"
                    profession.text = subject.profession.toLowerCase().capitalize()

                }, { err -> err.printStackTrace() })

        profileViewModel.getComments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    profileCommentAdapter.commentList.add(it)
                    profileCommentAdapter
                            .notifyItemInserted(profileCommentAdapter.commentList.size - 1)
                }, { err -> err.printStackTrace() })

        profileViewModel.getPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    profilePostAdapter.postList.add(it)
                    profilePostAdapter
                            .notifyItemInserted(profilePostAdapter.postList.size - 1)
                }, { err -> err.printStackTrace() })

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0)
                    showPosts()
                else
                    showComments()
            }
        })


    }

    private fun showPosts() {
        profilePostRecyclerView.visibility = View.VISIBLE
        profileCommentRecyclerView.visibility = View.GONE

    }

    private fun showComments() {
        profileCommentRecyclerView.visibility = View.VISIBLE
        profilePostRecyclerView.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSelfProfileFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnSelfProfileFragmentInteractionListener {
        fun openProfile(birthId: String)
        fun openFullPost(post: Post)
    }

    companion object {
        @JvmStatic
        fun newInstance(birthId: String) =
                SelfProfileFragment().apply {
                    arguments = Bundle().apply {
                        this.putString("birthId", birthId)
                    }
                }
    }
}
