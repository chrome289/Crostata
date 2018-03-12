package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.modelView.MainActivityViewModel
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.CommunityFragment
import xyz.siddharthseth.crostata.view.ui.fragment.HomeFeedFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ViewPostFragment


class MainActivity : AppCompatActivity()
        , HomeFeedFragment.OnFragmentInteractionListener
        , ProfileFragment.OnFragmentInteractionListener
        , CommunityFragment.OnFragmentInteractionListener
        , ViewPostFragment.OnFragmentInteractionListener {

    override fun showBottomNavigation(shouldShow: Boolean) {
        if (shouldShow)
            bottomNavigationView.visibility = View.VISIBLE
        else
            bottomNavigationView.visibility = View.GONE
    }

    override fun openFullPost(post: Post) {
        val fragment = supportFragmentManager.findFragmentByTag("viewPostFragment")
        if (fragment == null) {
            viewPostFragment = ViewPostFragment.newInstance(post)
        } else {
            viewPostFragment = fragment as ViewPostFragment
        }
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.frame, viewPostFragment, "viewPostFragment")
                .addToBackStack("viewPostFragment")
                .commit()
        mainActivityViewModel.lastSelectedId = 3
    }

    private val TAG = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var viewPostFragment: ViewPostFragment

    private lateinit var mainActivityViewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        init()
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menu?.clear()
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
*/
    private fun init() {

        toolbar.title = ""
        toolbarTitle.text = resources.getString(R.string.toolbar_home)

        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            Log.v(TAG, item.order.toString())
            when (item.order) {
                0 -> {
                    val fragment = supportFragmentManager.findFragmentByTag("homeFragment")
                    if (fragment == null) {
                        homeFeedFragment = HomeFeedFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.frame, homeFeedFragment, "homeFragment")
                                .addToBackStack("homeFragment")
                                .commit()
                    } else {
                        homeFeedFragment = fragment as HomeFeedFragment
                    }
                    mainActivityViewModel.lastSelectedId = R.id.home
                    toolbarTitle.text = resources.getString(R.string.toolbar_home)
                }
                1 -> {
                    val fragment = supportFragmentManager.findFragmentByTag("communityFragment")
                    if (fragment == null) {
                        communityFragment = CommunityFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.frame, communityFragment, "communityFragment")
                                .addToBackStack("communityFragment")
                                .commit()
                    } else {
                        communityFragment = fragment as CommunityFragment
                    }
                    mainActivityViewModel.lastSelectedId = R.id.community
                    toolbarTitle.text = resources.getString(R.string.toolbar_community)
                }
                2 -> {
                    val fragment = supportFragmentManager.findFragmentByTag("profileFragment")
                    if (fragment == null) {
                        profileFragment = ProfileFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(R.id.frame, profileFragment, "profileFragment")
                                .addToBackStack("profileFragment")
                                .commit()
                    } else {
                        profileFragment = fragment as ProfileFragment
                    }

                    mainActivityViewModel.lastSelectedId = R.id.profile
                    toolbarTitle.text = resources.getString(R.string.toolbar_profile)
                }
            }
            true
        }
        bottomNavigationView.selectedItemId = mainActivityViewModel.lastSelectedId
        showBottomNavigation(true)

        addPostButton.setOnClickListener { v: View -> startActivity(Intent(this, AddPostActivity::class.java)) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0)
            finish()
    }
}