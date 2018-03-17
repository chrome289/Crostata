package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.modelView.ViewPostViewModel

class ViewPostFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var viewPostViewModel: ViewPostViewModel
    val TAG = "ViewPostFragment"
    fun onButtonPressed(uri: Uri) {
        /*if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }*/
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
        mListener?.showBottomNavigation(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_post, container, false)
    }

    override fun onResume() {
        super.onResume()

        viewPostViewModel = ViewModelProviders.of(this).get(ViewPostViewModel::class.java)

        Log.v(TAG, "onResume")
        mListener?.showBottomNavigation(false)
        mListener?.showToolbarMain(false)
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onResume")

        mListener?.showBottomNavigation(true)
        mListener?.showToolbarMain(true)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun showBottomNavigation(shouldShow: Boolean)
        fun showToolbarMain(shouldShow: Boolean)
    }

    private var post: Post = Post()

    companion object {
        fun newInstance(post: Post): ViewPostFragment {
            val fragment = ViewPostFragment()
            fragment.post = post
            return fragment
        }
    }
}