package xyz.siddharthseth.crostata.view.ui.activity

import android.app.AlertDialog
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_navigation_drawer.view.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.R.layout.activity_main
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.data.model.retrofit.Token
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.viewModel.PostInteractionListener
import xyz.siddharthseth.crostata.util.viewModel.ProfileInteractionListener
import xyz.siddharthseth.crostata.view.ui.fragment.CommunityFragment
import xyz.siddharthseth.crostata.view.ui.fragment.HomeFeedFragment
import xyz.siddharthseth.crostata.view.ui.fragment.VigilanceFragment
import xyz.siddharthseth.crostata.viewmodel.activity.MainActivityViewModel


class MainActivity : AppCompatActivity()
        , CommunityFragment.OnFragmentInteractionListener
        , VigilanceFragment.OnFragmentInteractionListener
        , ProfileInteractionListener
        , PostInteractionListener
        , View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.refreshButton -> {
                isNetAvailable()
            }
        }
    }

    override fun isNetAvailable() {
        mainActivityViewModel.checkNetworkAvailable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mutableNetStatusChanged.value = true
                }, {
                    mutableNetStatusChanged.value = false
                    it.printStackTrace()
                })
    }

    override fun showError(isShown: Boolean) {
        errorLayout.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showLoader(isShown: Boolean) {
        loadingFrame.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showAnimation(isShown: Boolean) {
        if (isShown) {
            animationView.setAnimation(R.raw.loader1)
            animationView.scale = 0.2f
            animationView.visibility = View.VISIBLE
            animationView.playAnimation()
        } else {
            animationView.cancelAnimation()
            animationView.visibility = View.GONE
        }
    }

    override fun setLoaderVisibility(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
        Log.d(TAG, "setLoaderVisibility $isLoaderVisible $isAnimationVisible $isErrorVisible")

        showLoader(isLoaderVisible)
        showAnimation(isAnimationVisible)
        showError(isErrorVisible)
        //swipeRefresh.isRefreshing = false
    }

    override fun openProfile(birthId: String, name: String) {
        val intent = Intent(this, DetailActivity::class.java)
        if (LoggedSubject.birthId == birthId) {
            intent.putExtra("birthId", birthId)
            intent.putExtra("name", "My Profile")
        } else {
            intent.putExtra("birthId", birthId)
            intent.putExtra("name", name)
        }
        if (!mainActivityViewModel.isDetailActivityOpen) {
            mainActivityViewModel.isDetailActivityOpen = true
            startActivityForResult(intent, DETAIL_ACTIVITY_RESULT_CODE)
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
                R.id.search -> {
                }
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
        LoggedSubject.clear(applicationContext)
        val sharedPreferences = SharedPreferencesService()
        sharedPreferences.saveToken(Token(), applicationContext)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun openFullPost(post: Post) {
        Log.v(TAG, "activity click listener")
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("post", post)

        if (!mainActivityViewModel.isDetailActivityOpen) {
            mainActivityViewModel.isDetailActivityOpen = true
            startActivityForResult(intent, DETAIL_ACTIVITY_RESULT_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        refreshButton.setOnClickListener(this)
        setupToolbarAndDrawer()
        setNavigationHeader()
    }


    private fun setNavigationHeader() {
        navigationView.getHeaderView(0).profileName.text = LoggedSubject.name
        GlideApp.with(this)
                .load(getProfileImageLink())
                .priority(Priority.LOW)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(navigationView.getHeaderView(0).profileImage)

    }

    private fun getProfileImageLink(): GlideUrl {
        return GlideUrl(getString(R.string.server_url) +
                "subject/profileImage?birthId=${LoggedSubject.birthId}&dimen=320&quality=80"
                , LazyHeaders.Builder()
                .addHeader("authorization", mainActivityViewModel.getToken())
                .build())
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)

        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            navigationDrawerListener(it)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            true
        }
        navigationDrawerListener(navigationView.menu.getItem(0))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DETAIL_ACTIVITY_RESULT_CODE -> {
                if (data != null) {
                    if (data.hasExtra("openItem")) {
                        Log.d(TAG, "i am not working 2")
                        val openItemId = data.getIntExtra("openItem", R.id.home)
                        navigationDrawerListener(navigationView.menu.findItem(openItemId))
                    }
                }
                mainActivityViewModel.isDetailActivityOpen = false
            }
        }
    }

    private fun navigationDrawerListener(item: MenuItem): Boolean {
        // Log.v(TAG, item.order.toString())
        when {
            item.itemId == R.id.addPost -> addNewPost()
            item.itemId == R.id.signOut -> showSignOutDialog()
            item.itemId == R.id.profile -> openProfile(LoggedSubject.birthId, "My Profile")
            else -> {
                val fragment = when (item.itemId) {
                    R.id.community -> {
                        getFragment(R.id.community)
                    }
                    R.id.vigilance -> {
                        getFragment(R.id.vigilance)
                    }
                    else -> {
                        getFragment(R.id.home)
                    }
                }
                navigationView.menu.findItem(item.itemId).isChecked = true

                performFragmentTransaction(fragment, item.itemId.toString())
            }
        }
        return true
    }

    private fun performFragmentTransaction(fragment: Fragment, fragmentTag: String) {
        val fragmentEntry = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragmentEntry != null) {
            val transaction = supportFragmentManager.beginTransaction()
            for (tempFragment in supportFragmentManager.fragments) {
                if (tempFragment.tag != fragmentTag)
                    transaction.hide(tempFragment)
            }
            transaction.show(fragmentEntry)
                    .commit()
        } else {
            supportFragmentManager.beginTransaction()
                    .add(R.id.frame, fragment, fragmentTag)
                    .commit()
        }
    }

    private fun getFragment(fragmentId: Int): Fragment {
        Log.v(TAG, "toolbar ${mainActivityViewModel.getToolbarTitle(fragmentId)}")
        titleTextView.text = mainActivityViewModel.getToolbarTitle(fragmentId)

        return when (fragmentId) {
            R.id.community -> {
                if (communityFragment == null) {
                    communityFragment = CommunityFragment.newInstance()
                    communityFragment as CommunityFragment
                } else {
                    communityFragment as CommunityFragment
                }
            }
            R.id.vigilance -> {
                if (vigilanceFragment == null) {
                    vigilanceFragment = VigilanceFragment.newInstance()
                    vigilanceFragment as VigilanceFragment
                } else {
                    vigilanceFragment as VigilanceFragment
                }
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

    private val TAG: String = javaClass.simpleName
    private var homeFeedFragment: HomeFeedFragment? = null
    private var communityFragment: CommunityFragment? = null
    private var vigilanceFragment: VigilanceFragment? = null

    private var isMainToolbarMenuShown = true
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override var mutableNetStatusChanged = MutableLiveData<Boolean>()

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val DETAIL_ACTIVITY_RESULT_CODE: Int = 0
}