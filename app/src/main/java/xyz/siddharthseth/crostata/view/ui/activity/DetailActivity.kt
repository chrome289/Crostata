package xyz.siddharthseth.crostata.view.ui.activity

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_detail.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.util.viewModel.ProfileInteractionListener
import xyz.siddharthseth.crostata.util.viewModel.ViewPostInteractionListener
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ViewPostFragment
import xyz.siddharthseth.crostata.viewmodel.activity.DetailActivityViewModel

class DetailActivity : AppCompatActivity()
        , ViewPostInteractionListener
        , ProfileInteractionListener {

    override fun openFullPost(post: Post) {
        viewPostFragment = ViewPostFragment.newInstance(post)

        detailActivityViewModel.toolbarTitle.add(getString(R.string.comments))
        titleTextView.text = getString(R.string.comments)

        supportFragmentManager.beginTransaction()
                .add(R.id.detailFrame, viewPostFragment, R.id.viewPost.toString())
                .addToBackStack(null)
                .commit()
    }

    override fun openProfile(birthId: String) {
        if (LoggedSubject.birthId == birthId) {
            setProfileNavigation()
            finish()
        } else {
            profileFragment = ProfileFragment.newInstance(birthId)

            detailActivityViewModel.toolbarTitle.add(getString(R.string.profile))
            titleTextView.text = getString(R.string.profile)

            supportFragmentManager.beginTransaction()
                    .add(R.id.detailFrame, profileFragment, R.id.profile.toString())
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun isNetAvailable() {
        detailActivityViewModel.checkNetworkAvailable()
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

    override var mutableNetStatusChanged = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        detailActivityViewModel = ViewModelProviders.of(this).get(DetailActivityViewModel::class.java)

        if (intent.hasExtra("post")) {
            openFullPost(intent.getParcelableExtra("post"))
        } else if (intent.hasExtra("birthId")) {
            val birthId = intent.getStringExtra("birthId")
            openProfile(birthId)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate()

            detailActivityViewModel.toolbarTitle
                    .removeAt(detailActivityViewModel.toolbarTitle.size - 1)
            val temp = detailActivityViewModel.toolbarTitle.last()
            titleTextView.text = temp
        } else {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        setResult(Activity.RESULT_OK, intentData)
    }

    private fun setProfileNavigation() {
        intentData.putExtra("openItem", R.id.selfProfile)
    }

    private var viewPostFragment: ViewPostFragment? = null
    private var profileFragment: ProfileFragment? = null
    private val intentData = Intent()
    private val TAG: String = javaClass.simpleName
    private lateinit var detailActivityViewModel: DetailActivityViewModel
}
