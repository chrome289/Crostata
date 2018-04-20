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
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.util.backstack.BackStackListener
import xyz.siddharthseth.crostata.util.backstack.BackStackManager
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.*
import xyz.siddharthseth.crostata.viewmodel.activity.MainActivityViewModel


class MainActivity : AppCompatActivity()
        , HomeFeedFragment.OnFragmentInteractionListener
        , ProfileFragment.OnProfileFragmentInteractionListener
        , CommunityFragment.OnFragmentInteractionListener
        , VigilanceFragment.OnFragmentInteractionListener
        , SelfProfileFragment.OnSelfProfileFragmentInteractionListener
        , ViewPostFragment.OnFragmentInteractionListener, BackStackListener {

    private val TAG = javaClass.simpleName
    private var homeFeedFragment: HomeFeedFragment? = null
    private var selfProfileFragment: SelfProfileFragment? = null
    private var profileFragment: ProfileFragment? = null
    private var communityFragment: CommunityFragment? = null
    private var vigilanceFragment: VigilanceFragment? = null
    private var viewPostFragment: ViewPostFragment? = null

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var customFragmentManager: BackStackManager

    override fun finishActivity() {
        finish()
    }

    override fun tabReselected() {}

    override fun openProfile(birthId: String) {
        val bundle = Bundle()
        if (LoggedSubject.birthId == birthId) {
            bottomNavigationView.selectedItemId = R.id.profile
        } else {
            bundle.putString("birthId", birthId)
            customFragmentManager.addChildFragment(getFragment(R.id.selfProfile, bundle), R.id.frame)
        }
    }

    override fun addNewPost() {
        startActivity(Intent(this, AddPostActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun openFullPost(post: Post) {
        Log.v(TAG, "activity click listener")
        val bundle = Bundle()
        bundle.putParcelable("post", post)

        val fragment = getFragment(R.id.viewPost, bundle)
        customFragmentManager.addChildFragment(fragment, R.id.frame)
    }

    private fun setViewPostToolbar() {
        val toolbar = supportActionBar
        if (toolbar != null) {
            toolbar.title = "Comments"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        customFragmentManager = BackStackManager(this, supportFragmentManager)

        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { getBottomNavigationListener(it) }
        bottomNavigationView.selectedItemId = R.id.home
    }

    private fun getBottomNavigationListener(item: MenuItem): Boolean {
        Log.v(TAG, item.order.toString())
        val fragment = when (item.itemId) {
            R.id.community -> {
                getFragment(R.id.community, null)
            }
            R.id.profile -> {
                getFragment(R.id.profile, null)
            }
            R.id.vigilance -> {
                getFragment(R.id.vigilance, null)
            }
            else -> {
                getFragment(R.id.home, null)
            }
        }
        customFragmentManager.addRootFragment(fragment, item.itemId.toString(), R.id.frame)
        return true
    }


    override fun onBackPressed() {
        val fragment = customFragmentManager.onBackPressed()
        if (fragment != null) {
            //Log.v(TAG, fragment.tag!!.toInt().toString() + " " + R.id.home)
            bottomNavigationView.setOnNavigationItemSelectedListener { true }
            bottomNavigationView.selectedItemId = fragment.tag!!.toInt()
            bottomNavigationView.setOnNavigationItemSelectedListener { getBottomNavigationListener(it) }
        }
    }

    private fun getFragment(fragmentId: Int, bundle: Bundle?): Fragment {
        when (fragmentId) {
            R.id.community -> {
                return if (communityFragment == null) {
                    communityFragment = CommunityFragment.newInstance()
                    communityFragment as CommunityFragment
                } else {

                    communityFragment as CommunityFragment
                }
            }
            R.id.profile -> {
                return if (profileFragment == null) {
                    profileFragment = ProfileFragment.newInstance()
                    profileFragment as ProfileFragment
                } else {

                    profileFragment as ProfileFragment
                }
            }
            R.id.vigilance -> {
                return if (vigilanceFragment == null) {
                    vigilanceFragment = VigilanceFragment.newInstance()
                    vigilanceFragment as VigilanceFragment
                } else {

                    vigilanceFragment as VigilanceFragment
                }
            }
            R.id.viewPost -> {
                if (bundle != null) {
                    val post: Post = bundle.getParcelable("post")
                    viewPostFragment = ViewPostFragment.newInstance(post)
                }
                return viewPostFragment as ViewPostFragment
            }
            R.id.selfProfile -> {
                if (bundle != null) {
                    val birthId: String = bundle.getString("birthId")
                    selfProfileFragment = SelfProfileFragment.newInstance(birthId)
                }
                return selfProfileFragment as SelfProfileFragment
            }

            else -> {
                return if (homeFeedFragment == null) {
                    homeFeedFragment = HomeFeedFragment.newInstance()
                    homeFeedFragment as HomeFeedFragment
                } else {

                    homeFeedFragment as HomeFeedFragment
                }
            }
        }
    }
}