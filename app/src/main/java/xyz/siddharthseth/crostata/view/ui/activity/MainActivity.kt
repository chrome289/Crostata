package xyz.siddharthseth.crostata.view.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.*


class MainActivity : AppCompatActivity()
        , HomeFeedFragment.OnFragmentInteractionListener
        , ProfileFragment.OnFragmentInteractionListener
        , SearchFragment.OnFragmentInteractionListener
        , CommunityFragment.OnFragmentInteractionListener
        , PostFragment.OnFragmentInteractionListener {

    override fun showBottomNavigation(shouldShow: Boolean) {
        if (shouldShow)
            bottomNavigationView.visibility = View.VISIBLE
        else
            bottomNavigationView.visibility = View.GONE
    }

    override fun openFullPost(post: Post) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.frame, postFragment)
                .addToBackStack("postFragment")
                .commit()
    }


    private val TAG = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var postFragment: PostFragment
    private var lastSelectedId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

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
        homeFeedFragment = HomeFeedFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()
        communityFragment = CommunityFragment.newInstance()
        searchFragment = SearchFragment.newInstance()
        postFragment = PostFragment.newInstance()

        /* supportFragmentManager.beginTransaction()
                 .add(R.id.frame, homeFeedFragment)
                 .addToBackStack("homeFragment")
                 .commit()
 */
        toolbar.title = ""
        toolbarTitle.text = resources.getString(R.string.toolbar_home)

        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            Log.v(TAG, item.order.toString())
            when (item.order) {
                0 -> {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, homeFeedFragment)
                            .addToBackStack("homeFragment")
                            .commit()

                    toolbarTitle.text = resources.getString(R.string.toolbar_home)
                }
                1 -> {
                    if (lastSelectedId > 1) {
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.frame, communityFragment)
                                .addToBackStack("communityFragment")
                                .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(R.id.frame, communityFragment)
                                .addToBackStack("communityFragment")
                                .commit()
                    }
                    toolbarTitle.text = resources.getString(R.string.toolbar_community)
                }
                2 -> {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.frame, profileFragment)
                            .addToBackStack("profileFragment")
                            .commit()

                    toolbarTitle.text = resources.getString(R.string.toolbar_profile)
                }

            }
            true
        }

        bottomNavigationView.selectedItemId = R.id.home
    }

    fun submitVote() {

    }
}