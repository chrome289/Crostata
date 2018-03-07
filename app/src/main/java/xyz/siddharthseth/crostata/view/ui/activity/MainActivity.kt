package xyz.siddharthseth.crostata.view.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.HomeFeedFragment


class MainActivity : AppCompatActivity(), HomeFeedFragment.OnFragmentInteractionListener {

    private val TAG = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment

    override fun onFragmentInteraction(view: View, adapterPosition: Int) {
        when (view.id) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menu?.clear()
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun init() {
        homeFeedFragment = HomeFeedFragment.newInstance()

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .add(R.id.frame, homeFeedFragment)
                .commit()

        toolbar.title = ""
        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.selectedItemId = 0
    }

    fun submitVote() {

    }
}