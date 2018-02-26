package xyz.siddharthseth.crostata.view.ui

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import rx.subscriptions.CompositeSubscription
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.modelView.HomeFeedViewModel
import xyz.siddharthseth.crostata.view.ui.fragments.HomeFeedFragment

class MainActivity : AppCompatActivity(), HomeFeedFragment.OnFragmentInteractionListener {

    private val TAG = "MainActivity"
    private var homeFeedViewModel = HomeFeedViewModel()
    private var compositeSubscription = CompositeSubscription()

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        init()
    }

    private fun init() {
        val homeFeedFragment = HomeFeedFragment.newInstance()
        val fragmentManager = fragmentManager
        fragmentManager.beginTransaction()
                .add(R.id.frame, homeFeedFragment)
                .commit()


        homeFeedViewModel.getNextPosts(this).subscribe({ post ->
            Log.v(TAG, "got a post")
            homeFeedFragment.addNewPost(post)
        }, { onError ->
            onError.printStackTrace()
            Log.v(TAG, "error   " + onError.stackTrace + "   " + onError.localizedMessage + "    " + onError.cause)
        })
    }
}