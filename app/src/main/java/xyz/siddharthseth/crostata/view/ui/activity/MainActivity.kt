package xyz.siddharthseth.crostata.view.ui.activity

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Token
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.backstack.BackStackListener
import xyz.siddharthseth.crostata.util.backstack.BackStackManager
import xyz.siddharthseth.crostata.util.viewModel.PostInteractionListener
import xyz.siddharthseth.crostata.util.viewModel.ProfileInteractionListener
import xyz.siddharthseth.crostata.util.viewModel.ViewPostInteractionListener
import xyz.siddharthseth.crostata.view.ui.customView.BottomNavigationViewHelper
import xyz.siddharthseth.crostata.view.ui.fragment.*
import xyz.siddharthseth.crostata.viewmodel.activity.MainActivityViewModel

class MainActivity : AppCompatActivity()
        , CommunityFragment.OnFragmentInteractionListener
        , VigilanceFragment.OnFragmentInteractionListener
        , BackStackListener
        , ViewPostInteractionListener
        , ProfileInteractionListener
        , PostInteractionListener {

    override fun showBackButton(isShown: Boolean) {
        val toolbar: ActionBar = this.supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(isShown)
        toolbar.setDisplayShowHomeEnabled(isShown)
        isMainToolbarMenuShown = !isShown
        invalidateOptionsMenu()
    }

    override fun showNavBar(isShown: Boolean) {
        bottomNavigationView.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun finishActivity() {
        finish()
    }

    override fun tabReselected() {}

    override fun openProfile(birthId: String) {
        val bundle = Bundle()
        if (LoggedSubject.birthId == birthId) {
            bottomNavigationView.selectedItemId = R.id.selfProfile
        } else {
            bundle.putString("birthId", birthId)
            customFragmentManager.addChildFragment(getFragment(R.id.profile, bundle), R.id.frame)
        }
    }

    override fun addNewPost() {
        startActivity(Intent(this, AddPostActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        if (isMainToolbarMenuShown)
            menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        else
            menuInflater.inflate(R.menu.menu_toolbar_view_post, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item != null) {
            when (item.itemId) {
                R.id.addPost -> addNewPost()
                R.id.search -> {
                }
                R.id.signOut -> showSignOutDialog()
                android.R.id.home -> this.onBackPressed()
            }
            true
        } else {
            false
        }
    }

    private fun showSignOutDialog() {
        val alertDialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton("OKAY", { dialog, _ ->
                    dialog.dismiss()
                    signOut()
                })
                .setNegativeButton("CANCEL", { dialog, _ -> dialog.dismiss() })
                .setTitle("Sign Out ?")
                .setMessage("Close the session and sign out.")
                .create()

        alertDialog.show()
    }

    private fun signOut() {
        LoggedSubject.init("", "")
        val sharedPreferences = SharedPreferencesService()
        sharedPreferences.saveToken(Token(), applicationContext)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun openFullPost(post: Post) {
        Log.v(TAG, "activity click listener")
        val bundle = Bundle()
        bundle.putParcelable("post", post)

        val fragment = getFragment(R.id.viewPost, bundle)
        customFragmentManager.addChildFragment(fragment, R.id.frame)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        customFragmentManager = BackStackManager(this, supportFragmentManager)

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)

        //workaround, dummy load
        bottomNavigationView.selectedItemId = R.id.community
        bottomNavigationView.setOnNavigationItemSelectedListener {
            Log.v(TAG, "loading once")
            getBottomNavigationListener(it)
        }
        bottomNavigationView.setOnNavigationItemReselectedListener { Log.v(TAG, "no reloading for you") }
        bottomNavigationView.selectedItemId = R.id.home
    }

    override fun onBackPressed() {
        val fragment = customFragmentManager.onBackPressed()
        if (fragment != null) {
            //Log.v(TAG, fragment.tag!!.toInt().toString() + " " + R.id.home)
            bottomNavigationView.setOnNavigationItemSelectedListener { true }
            bottomNavigationView.selectedItemId = fragment.tag!!.toInt()
        }
    }

    private fun getBottomNavigationListener(item: MenuItem): Boolean {
        Log.v(TAG, item.order.toString())
        val fragment = when (item.itemId) {
            R.id.community -> {
                getFragment(R.id.community, null)
            }
            R.id.selfProfile -> {
                getFragment(R.id.selfProfile, null)
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

    private fun getFragment(fragmentId: Int, bundle: Bundle?): Fragment {
        return when (fragmentId) {
            R.id.community -> {
                if (communityFragment == null) {
                    communityFragment = CommunityFragment.newInstance()
                    communityFragment as CommunityFragment
                } else {
                    communityFragment as CommunityFragment
                }
            }
            R.id.profile -> {
                profileFragment = ProfileFragment.newInstance(bundle!!)
                profileFragment as ProfileFragment
            }
            R.id.vigilance -> {
                if (vigilanceFragment == null) {
                    vigilanceFragment = VigilanceFragment.newInstance()
                    vigilanceFragment as VigilanceFragment
                } else {
                    vigilanceFragment as VigilanceFragment
                }
            }
            R.id.viewPost -> {
                viewPostFragment = ViewPostFragment.newInstance(bundle!!)
                viewPostFragment as ViewPostFragment
            }
            R.id.selfProfile -> {
                //if (selfProfileFragment == null) {
                selfProfileFragment = SelfProfileFragment.newInstance()
                selfProfileFragment as SelfProfileFragment
                /*} else {
                    selfProfileFragment as SelfProfileFragment
                }*/
            }
            else -> {
                if (homeFeedFragment == null) {
                    homeFeedFragment = HomeFeedFragment.newInstance()
                    homeFeedFragment as HomeFeedFragment
                } else {
                    homeFeedFragment as HomeFeedFragment
                }
            }
        }
    }

    private val TAG = javaClass.simpleName
    private var homeFeedFragment: HomeFeedFragment? = null
    private var selfProfileFragment: SelfProfileFragment? = null
    private var profileFragment: ProfileFragment? = null
    private var communityFragment: CommunityFragment? = null
    private var vigilanceFragment: VigilanceFragment? = null
    private var viewPostFragment: ViewPostFragment? = null

    var isMainToolbarMenuShown = true
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var customFragmentManager: BackStackManager
}