package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.CommunityFragment
import xyz.siddharthseth.crostata.view.ui.fragment.HomeFeedFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ViewPostFragment
import xyz.siddharthseth.crostata.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity()
        , HomeFeedFragment.OnFragmentInteractionListener
        , ProfileFragment.OnFragmentInteractionListener
        , CommunityFragment.OnFragmentInteractionListener
        , ViewPostFragment.OnFragmentInteractionListener {

    private val TAG = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var viewPostFragment: ViewPostFragment

    private lateinit var selectedPost: Post

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun showToolbarMain(shouldShow: Boolean) {
        if (shouldShow) {
            toolbarMain.visibility = View.VISIBLE
            toolbarViewPost.visibility = View.GONE

            toolbar.navigationIcon = null
            toolbar.setNavigationOnClickListener { null }
        } else {
            toolbarMain.visibility = View.GONE
            toolbarViewPost.visibility = View.VISIBLE

            toolbar.navigationIcon = getDrawable(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { it -> supportFragmentManager.popBackStack() }
        }
    }

    override fun showBottomNavigation(shouldShow: Boolean) {
        if (shouldShow)
            bottomNavigationView.visibility = View.VISIBLE
        else
            bottomNavigationView.visibility = View.GONE
    }

    override fun openFullPost(post: Post) {
        selectedPost = post
        openFragment(getFragment(R.id.viewPost))
        mainActivityViewModel.addToFragmentStack(R.id.viewPost)
        // toolbarTitle.text = mainActivityViewModel.getToolbarTitle(R.id.viewPost)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        setSupportActionBar(toolbar)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        toolbar.title = ""
        toolbarTitle.text = resources.getString(R.string.toolbar_home)

        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            Log.v(TAG, item.order.toString())
            when (item.itemId) {
                R.id.home -> {
                    openFragment(getFragment(R.id.home))
                    mainActivityViewModel.addToFragmentStack(R.id.home)
                    toolbarTitle.text = mainActivityViewModel.getToolbarTitle(R.id.home)
                }
                R.id.community -> {
                    openFragment(getFragment(R.id.community))
                    mainActivityViewModel.addToFragmentStack(R.id.community)
                    toolbarTitle.text = mainActivityViewModel.getToolbarTitle(R.id.community)
                }
                R.id.profile -> {
                    openFragment(getFragment(R.id.profile))
                    mainActivityViewModel.addToFragmentStack(R.id.profile)
                    toolbarTitle.text = mainActivityViewModel.getToolbarTitle(R.id.profile)
                }
            }
            true
        }
        bottomNavigationView.selectedItemId = mainActivityViewModel.lastSelectedId
        showBottomNavigation(true)

        addPostButton.setOnClickListener { v: View -> startActivity(Intent(this, AddPostActivity::class.java)) }
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
        if (mainActivityViewModel.isInitialized) {
            transaction.setCustomAnimations(R.anim.shift_up, R.anim.blank)
        } else {
            mainActivityViewModel.isInitialized = true
        }
        transaction.replace(R.id.frame, fragment)
                .addToBackStack(fragment.tag)
        transaction.commit()
        Log.v(TAG, "done")
    }


    private fun getFragment(fragmentId: Int): Fragment {
        when (fragmentId) {
            R.id.community -> {
                val fragment = supportFragmentManager.findFragmentByTag(CommunityFragment::class.java.name)
                if (fragment == null) {
                    communityFragment = CommunityFragment.newInstance()
                } else {
                    communityFragment = fragment as CommunityFragment
                }
                return communityFragment
            }
            R.id.profile -> {
                val fragment = supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.name)
                if (fragment == null) {
                    profileFragment = ProfileFragment.newInstance()
                } else {
                    profileFragment = fragment as ProfileFragment
                }
                return profileFragment
            }
            R.id.viewPost -> {
                val fragment = supportFragmentManager.findFragmentByTag(ViewPostFragment::class.java.name)
                if (fragment == null) {
                    viewPostFragment = ViewPostFragment.newInstance(selectedPost)
                } else {
                    viewPostFragment = fragment as ViewPostFragment
                }
                return viewPostFragment
            }
            else -> {
                val fragment = supportFragmentManager.findFragmentByTag(HomeFeedFragment::class.java.name)
                if (fragment == null) {
                    homeFeedFragment = HomeFeedFragment.newInstance()
                } else {
                    homeFeedFragment = fragment as HomeFeedFragment
                }
                return homeFeedFragment
            }
        }
    }
}