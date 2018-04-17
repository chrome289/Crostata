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
import xyz.siddharthseth.crostata.util.CustomFragmentBackStack
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.*
import xyz.siddharthseth.crostata.viewmodel.activity.MainActivityViewModel


class MainActivity : AppCompatActivity()
        , HomeFeedFragment.OnFragmentInteractionListener
        , ProfileFragment.OnProfileFragmentInteractionListener
        , CommunityFragment.OnFragmentInteractionListener
        , VigilanceFragment.OnFragmentInteractionListener
        , ViewPostFragment.OnFragmentInteractionListener {

    override fun openProfile(birthId: String) {
        val bundle = Bundle()
        bundle.putString("birthId", birthId)
        if (LoggedSubject.birthId == birthId)
            bundle.putBoolean("isOpeningSelf", true)
        else
            bundle.putBoolean("isOpeningSelf", false)

        val fragmentEntry = CustomFragmentBackStack.FragmentEntry(getFragment(R.id.profile2, bundle), R.id.profile2)
        mainActivityViewModel.addToFragmentStack(fragmentEntry)
        openFragment(fragmentEntry.fragment)
        CustomFragmentBackStack.printStack()
    }

    override fun addNewPost() {
        startActivity(Intent(this, AddPostActivity::class.java))
    }

    private val TAG = "MainActivity"
    private lateinit var homeFeedFragment: HomeFeedFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var vigilanceFragment: VigilanceFragment
    private lateinit var viewPostFragment: ViewPostFragment

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun openFullPost(post: Post) {
        Log.v(TAG, "activity click listener")
        val bundle = Bundle()
        bundle.putParcelable("post", post)

        val fragmentEntry = CustomFragmentBackStack.FragmentEntry(getFragment(R.id.viewPost, bundle), R.id.viewPost)
        mainActivityViewModel.addToFragmentStack(fragmentEntry)
        openFragment(fragmentEntry.fragment)

        CustomFragmentBackStack.printStack()
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

        setSupportActionBar(toolbar)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            Log.v(TAG, item.order.toString())
            val fragmentEntry: CustomFragmentBackStack.FragmentEntry = when (item.itemId) {
                R.id.community -> {
                    CustomFragmentBackStack.FragmentEntry(getFragment(R.id.community, null), R.id.community)
                }
                R.id.profile -> {
                    val bundle = Bundle()
                    bundle.putString("birthId", LoggedSubject.birthId)
                    bundle.putBoolean("isOpeningSelf", true)
                    CustomFragmentBackStack.FragmentEntry(getFragment(R.id.profile, bundle), R.id.profile)
                }
                R.id.vigilance -> {
                    CustomFragmentBackStack.FragmentEntry(getFragment(R.id.vigilance, null), R.id.vigilance)
                }
                else -> {
                    CustomFragmentBackStack.FragmentEntry(getFragment(R.id.home, null), R.id.home)
                }
            }
            mainActivityViewModel.addToFragmentStack(fragmentEntry)
            openFragment(fragmentEntry.fragment)
            CustomFragmentBackStack.printStack()
            true
        }
        bottomNavigationView.selectedItemId = mainActivityViewModel.lastSelectedId
    }


    override fun onBackPressed() {
        Log.v(TAG, "fragmentCustomStack.size  " + CustomFragmentBackStack.getSize())
        if (CustomFragmentBackStack.getSize() == 0) {
            super.onBackPressed()
        } else {
            CustomFragmentBackStack.popFragment()
            if (CustomFragmentBackStack.getSize() == 0) {
                super.onBackPressed()
            } else {
                val fragmentEntry = CustomFragmentBackStack.getLastFragmentEntry()
                bottomNavigationView.selectedItemId = fragmentEntry.fragmentId

                openFragment(fragmentEntry.fragment)
                CustomFragmentBackStack.printStack()
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        mainActivityViewModel.isInitialized = true
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
                .commit()
    }


    private fun getFragment(fragmentId: Int, bundle: Bundle?): Fragment {
        when (fragmentId) {
            R.id.community -> {
                val fragment: Fragment? = CustomFragmentBackStack.getFragmentEntryById(R.id.community)
                communityFragment = if (fragment == null) {
                    CommunityFragment.newInstance()
                } else {
                    fragment as CommunityFragment
                }
                return communityFragment
            }
            R.id.profile -> {
                val fragment: Fragment? = CustomFragmentBackStack.getFragmentEntryById(R.id.profile)
                if (fragment == null) {
                    if (bundle != null) {
                        val birthId: String = bundle.getString("birthId")
                        val isOpeningSelf: Boolean = bundle.getBoolean("isOpeningSelf")
                        profileFragment = ProfileFragment.newInstance(birthId, isOpeningSelf)
                    }
                } else {
                    fragment as ProfileFragment
                }
                return profileFragment
            }
            R.id.vigilance -> {
                val fragment: Fragment? = CustomFragmentBackStack.getFragmentEntryById(R.id.vigilance)
                vigilanceFragment = if (fragment == null) {
                    VigilanceFragment.newInstance()
                } else {
                    fragment as VigilanceFragment
                }
                return vigilanceFragment
            }
            R.id.viewPost -> {
                if (bundle != null) {
                    val post: Post = bundle.getParcelable("post")
                    viewPostFragment = ViewPostFragment.newInstance(post)
                }
                return viewPostFragment
            }
            R.id.profile2 -> {
                if (bundle != null) {
                    val birthId: String = bundle.getString("birthId")
                    val isOpeningSelf: Boolean = bundle.getBoolean("isOpeningSelf")
                    profileFragment = ProfileFragment.newInstance(birthId, isOpeningSelf)
                }
                return profileFragment
            }

            else -> {
                val fragment: Fragment? = CustomFragmentBackStack.getFragmentEntryById(R.id.home)
                homeFeedFragment = if (fragment == null)
                    HomeFeedFragment.newInstance()
                else {
                    fragment as HomeFeedFragment
                }
                return homeFeedFragment
            }
        }
    }
}