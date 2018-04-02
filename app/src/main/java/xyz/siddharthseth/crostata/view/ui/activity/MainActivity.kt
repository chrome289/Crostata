package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.CommunityFragment
import xyz.siddharthseth.crostata.view.ui.fragment.HomeFeedFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment
import xyz.siddharthseth.crostata.viewmodel.activity.MainActivityViewModel


class MainActivity : AppCompatActivity()
        , HomeFeedFragment.OnFragmentInteractionListener
        , ProfileFragment.OnFragmentInteractionListener
        , CommunityFragment.OnFragmentInteractionListener {
    override fun addNewPost() {
        startActivity(Intent(this, AddPostActivity::class.java))
    }

    private val TAG = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var communityFragment: CommunityFragment

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun openFullPost(post: Post) {
        val intent = Intent(this, ViewPostActivity::class.java)
        intent.putExtra("post", post)
        intent.putExtra("transitionName", post.postId)

        startActivity(intent)
        //  overridePendingTransition(R.anim.slide_up, R.anim.blank)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            Log.v(TAG, item.order.toString())
            when (item.itemId) {
                R.id.home -> {
                    openFragment(getFragment(R.id.home))
                    mainActivityViewModel.addToFragmentStack(R.id.home)
                }
                R.id.community -> {
                    openFragment(getFragment(R.id.community))
                    mainActivityViewModel.addToFragmentStack(R.id.community)
                }
                R.id.profile -> {
                    openFragment(getFragment(R.id.profile))
                    mainActivityViewModel.addToFragmentStack(R.id.profile)
                }
            }
            true
        }
        bottomNavigationView.selectedItemId = mainActivityViewModel.lastSelectedId
        //showBottomNavigation(true)
    }


    override fun onBackPressed() {
        Log.v(TAG, "fragmentCustomStack.size  " + mainActivityViewModel.fragmentCustomStack.size)
        if (mainActivityViewModel.fragmentCustomStack.size == 0) {
            super.onBackPressed()
        } else {
            mainActivityViewModel.fragmentCustomStack.removeAt(mainActivityViewModel.fragmentCustomStack.size - 1)
            if (mainActivityViewModel.fragmentCustomStack.size > 0) {
                val fragmentId = mainActivityViewModel.fragmentCustomStack.last()
                mainActivityViewModel.addToBackStack = false
                bottomNavigationView.selectedItemId = fragmentId
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        Log.v(TAG, "openFragment")
        val transaction = supportFragmentManager.beginTransaction()
        /*if (mainActivityViewModel.isInitialized) {
            transaction.setCustomAnimations(R.anim.shift_up, 0)
        } else {*/
        mainActivityViewModel.isInitialized = true
        //}
        transaction.replace(R.id.frame, fragment)
                .setReorderingAllowed(true)
                .commit()
        Log.v(TAG, "done")
    }


    private fun getFragment(fragmentId: Int): Fragment {
        when (fragmentId) {
            R.id.community -> {
                val fragment = supportFragmentManager.findFragmentByTag(CommunityFragment::class.java.name)
                communityFragment = if (fragment == null) {
                    CommunityFragment.newInstance()
                } else {
                    fragment as CommunityFragment
                }
                return communityFragment
            }
            R.id.profile -> {
                val fragment = supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.name)
                profileFragment = if (fragment == null) {
                    ProfileFragment.newInstance()
                } else {
                    fragment as ProfileFragment
                }
                return profileFragment
            }
            else -> {
                val fragment = supportFragmentManager.findFragmentByTag(HomeFeedFragment::class.java.name)
                homeFeedFragment = if (fragment == null) {
                    HomeFeedFragment.newInstance()
                } else {
                    fragment as HomeFeedFragment
                }
                return homeFeedFragment
            }
        }
    }
}