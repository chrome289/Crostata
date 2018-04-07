package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel


class ProfileFragment : Fragment() {

    private var mListener: OnProfileFragmentInteractionListener? = null
    lateinit var profileViewModel: ProfileViewModel
    var isInitialized = false
    var isDetailWindowOpen = false

    /* lateinit var profileCommentAdapter: ProfileCommentAdapter
     lateinit var profilePostAdapter: ProfilePostAdapter*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()

        if (!isInitialized) {
            profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

            /* profileCommentAdapter = ProfileCommentAdapter(profileViewModel)
             profileCommentAdapter.setHasStableIds(true)
             profileCommentRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
             profileCommentRecyclerView.adapter = profileCommentAdapter

             profilePostAdapter = ProfilePostAdapter(profileViewModel)
             profilePostAdapter.setHasStableIds(true)
             profilePostRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
             profilePostRecyclerView.adapter = profilePostAdapter*/

            profileViewModel.getInfo()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ subject: Subject ->
                        profileViewModel.loadProfileImage(profileImage, false)

                        profileName.text = subject.name.capitalize()

                        patriotIndex.text = subject.patriotIndex.toString()
                        postTotal.text = subject.posts.toString()
                        commentTotal.text = subject.comments.toString()

                    }, { err -> err.printStackTrace() })

            /* profileViewModel.getComments()
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
                     }, { err -> err.printStackTrace() })*/

            showPosts.setOnClickListener { showPosts() }

            showComments.setOnClickListener { showComments() }
/*
            infoLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            profilePostParent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            profilePostRecyclerView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)*/
        }
    }

    private fun showPosts() {
        val constraintSet = ConstraintSet()
        val theParent: ConstraintLayout = view!!.findViewById(R.id.theParent)
        constraintSet.clone(theParent)
        constraintSet.connect(infoLayout.id, ConstraintSet.TOP, theParent.id, ConstraintSet.TOP)
        constraintSet.clear(infoLayout.id, ConstraintSet.BOTTOM)
        constraintSet.applyTo(theParent)
        profilePostParent.visibility = View.VISIBLE
        isDetailWindowOpen = true
    }

    private fun showComments() {
        profileCommentParent.visibility = View.VISIBLE
        isDetailWindowOpen = true
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = if (context is OnProfileFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException((context!!.toString() + " must implement OnFragmentInteractionListener"))
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnProfileFragmentInteractionListener

    companion object {
        fun newInstance(): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}