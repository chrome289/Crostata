package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.CommunityFragment
import xyz.siddharthseth.crostata.view.ui.fragment.HomeFeedFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment
import xyz.siddharthseth.crostata.view.ui.fragment.VigilanceFragment
import xyz.siddharthseth.crostata.viewmodel.activity.MainActivityViewModel


class MainActivity : AppCompatActivity()
        , HomeFeedFragment.OnFragmentInteractionListener
        , ProfileFragment.OnProfileFragmentInteractionListener
        , CommunityFragment.OnFragmentInteractionListener
        , VigilanceFragment.OnFragmentInteractionListener {

    override fun bottomNavigationVisible(isVisible: Boolean) {
        /* if (bottomNavigationView.isShown && !isVisible) {
             bottomNavigationView. = View.GONE
         } else if (isVisible) {
             bottomNavigationView.visibility = View.VISIBLE
         }*/
    }

    override fun addNewPost() {
        startActivity(Intent(this, AddPostActivity::class.java))
    }

    private val TAG = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var vigilanceFragment: VigilanceFragment

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun openFullPost(post: Post) {
        val intent = Intent(this, ViewPostActivity::class.java)
        intent.putExtra("post", post)
        intent.putExtra("transitionName", post._id)

        startActivity(intent)
        //  overridePendingTransition(R.anim.slide_up, R.anim.blank)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        // bottomNavigationView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
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
                R.id.vigilance -> {
                    openFragment(getFragment(R.id.vigilance))
                    mainActivityViewModel.addToFragmentStack(R.id.vigilance)
                }
            }
            true
        }
        bottomNavigationView.selectedItemId = mainActivityViewModel.lastSelectedId
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
            R.id.vigilance -> {
                val fragment = supportFragmentManager.findFragmentByTag(VigilanceFragment::class.java.name)
                vigilanceFragment = if (fragment == null) {
                    VigilanceFragment.newInstance()
                } else {
                    fragment as VigilanceFragment
                }
                return vigilanceFragment
            }
            else -> {
                val fragment = supportFragmentManager.findFragmentByTag(HomeFeedFragment::class.java.name)
                homeFeedFragment = if (fragment == null) {
                    Log.v(TAG, "new instance")
                    HomeFeedFragment.newInstance()
                } else {
                    Log.v(TAG, "past instance")
                    fragment as HomeFeedFragment
                }
                return homeFeedFragment
            }
        }
    }
}