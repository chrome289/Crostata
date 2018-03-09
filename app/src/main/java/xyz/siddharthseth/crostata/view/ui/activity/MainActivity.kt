package xyz.siddharthseth.crostata.view.ui.activity

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.HomeFeedFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment


class MainActivity : AppCompatActivity(), HomeFeedFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    private val _tag = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onFragmentInteraction(view: View, adapterPosition: Int) {
        when (view.id) {
        }
    }


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

        supportFragmentManager.beginTransaction()
                .add(R.id.frame, homeFeedFragment)
                .commit()

        toolbar.title = ""
        toolbarTitle.text = resources.getString(R.string.toolbar_home)

        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.selectedItemId = 0
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            Log.v(_tag, item.order.toString())
            when (item.order) {
                0 -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.frame, homeFeedFragment)
                            .commit()

                    toolbarTitle.text = resources.getString(R.string.toolbar_home)
                }
                1 -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.frame, homeFeedFragment)
                            .commit()

                    toolbarTitle.text = resources.getString(R.string.toolbar_search)
                }
                2 -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.frame, homeFeedFragment)
                            .commit()

                    toolbarTitle.text = resources.getString(R.string.toolbar_community)
                }
                3 -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.frame, profileFragment)
                            .commit()

                    toolbarTitle.text = resources.getString(R.string.toolbar_profile)
                }

            }
            true
        }

    }

    fun submitVote() {

    }
}