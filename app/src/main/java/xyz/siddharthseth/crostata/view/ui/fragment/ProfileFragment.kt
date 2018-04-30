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
import kotlinx.android.synthetic.main.fragment_profile.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.util.viewModel.ProfileInteractionListener
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {

    private var profileInteractionListener: ProfileInteractionListener? = null
    private lateinit var profileViewModel: ProfileViewModel
    private var isInitialized = false
    val TAG: String = javaClass.simpleName
    var birthId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            birthId = arguments!!.getString("birthId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private val observer: Observer<Post> = Observer {
        Log.v(TAG, "observer called")
        if (it != null) {
            Log.v(TAG, "fragment click listener")
            profileInteractionListener?.openFullPost(it)
        }
    }

    /*private val observerBirthId: Observer<String> = Observer {
        Log.v(TAG, "birthid observer called")
        if (it != null) {
            profileInteractionListener?.openProfile(it)
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
            profileViewModel.glide = GlideApp.with(this)
            profileViewModel.birthId = birthId
            Log.v(TAG, "loading birthid " + profileViewModel.birthId)

            isInitialized = true
        }
        profileViewModel.mutablePost.observe(this, observer)

        profilePostRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        profilePostRecyclerView.adapter = profileViewModel.profilePostAdapter

        profileViewModel.getInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ subject: Subject ->
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

                }, { err -> err.printStackTrace() })
        profileViewModel.getPosts()
    }

    /*private fun showPosts() {
        profilePostRecyclerView.visibility = View.VISIBLE
        profileCommentRecyclerView.visibility = View.GONE

    }

    private fun showComments() {
        profileCommentRecyclerView.visibility = View.VISIBLE
        profilePostRecyclerView.visibility = View.GONE
    }
*/
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        profileInteractionListener = if (context is ProfileInteractionListener) {
            context
        } else {
            throw RuntimeException((context!!.toString() + " must implement OnFragmentInteractionListener"))
        }
    }

    override fun onDetach() {
        super.onDetach()
        profileInteractionListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        this.putString("birthId", bundle.getString("birthId"))
                    }
                }
    }
}