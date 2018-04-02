package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.view.adapter.HomeFeedAdapter
import xyz.siddharthseth.crostata.viewmodel.fragment.ProfileViewModel

class ProfileFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var profileViewModel: ProfileViewModel
    var isInitialized = false
    lateinit var profileCommentAdapter:

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

            homeFeedAdapter = HomeFeedAdapter(homeFeedViewModel)
            homeFeedAdapter.setHasStableIds(true)

            profileViewModel.getInfo()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ subject: Subject ->
                        profileViewModel.loadProfileImage(profileImage)

                        profileName.text = subject.name.capitalize()

                        patriotIndex.text = subject.patriotIndex.toString()
                        postTotal.text = subject.posts.toString()
                        commentTotal.text = subject.comments.toString()

                    }, { err -> err.printStackTrace() })

            profileViewModel.getComments()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({post -> homeFeedAdapter.postList.add(post)
                        homeFeedAdapter.notifyItemInserted(homeFeedAdapter.postList.size - 1)})
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException((context!!.toString() + " must implement OnFragmentInteractionListener"))
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnFragmentInteractionListener

    companion object {
        fun newInstance(): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}