package xyz.siddharthseth.crostata.view.ui.activity

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_detail.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.SnackbarMessage
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.util.viewModel.ProfileInteractionListener
import xyz.siddharthseth.crostata.util.viewModel.ViewPostInteractionListener
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ViewPostFragment
import xyz.siddharthseth.crostata.viewmodel.activity.DetailActivityViewModel

class DetailActivity : AppCompatActivity()
        , ViewPostInteractionListener
        , ProfileInteractionListener
        , View.OnClickListener {

    override fun showSnackbar(snackbarMessage: SnackbarMessage) {
        Snackbar.make(forTheSnackbar, snackbarMessage.message, snackbarMessage.duration)
                .show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            android.R.id.home -> onBackPressed()
        }
    }

    override fun openFullPost(post: Post) {
        val viewPostFragment = ViewPostFragment.newInstance(post)

        DetailActivityViewModel.toolbarTitle.add(getString(R.string.comments))
        titleTextView.text = getString(R.string.comments)

        supportFragmentManager.beginTransaction()
                .add(R.id.detailFrame, viewPostFragment, R.id.viewPost.toString())
                .addToBackStack(null)
                .commit()
    }

    override fun openProfile(birthId: String, name: String) {
        val profileFragment = ProfileFragment.newInstance(birthId)

        DetailActivityViewModel.toolbarTitle.add(name)
        titleTextView.text = name

        supportFragmentManager.beginTransaction()
                .add(R.id.detailFrame, profileFragment, R.id.profile.toString())
                .addToBackStack(null)
                .commit()
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
        errorLayoutDetail.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showLoader(isShown: Boolean) {
        loadingFrame.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    override fun showAnimation(isShown: Boolean) {
        if (isShown) {
            animationViewDetail.setAnimation(R.raw.loader1)
            animationViewDetail.scale = 0.2f
            animationViewDetail.visibility = View.VISIBLE
            animationViewDetail.playAnimation()
        } else {
            animationViewDetail.cancelAnimation()
            animationViewDetail.visibility = View.GONE
        }
    }

    override fun setLoaderVisibility(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
       showLoader(isLoaderVisible)
        showAnimation(isAnimationVisible)
        showError(isErrorVisible)
        //swipeRefresh.isRefreshing = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override var mutableNetStatusChanged = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        detailActivityViewModel = ViewModelProviders.of(this).get(DetailActivityViewModel::class.java)

        if (savedInstanceState == null) {
            //if post ectra is available open the post or open profile
            if (intent.hasExtra("post")) {
                openFullPost(intent.getParcelableExtra("post"))
            } else if (intent.hasExtra("birthId")) {
                val birthId = intent.getStringExtra("birthId")
                val name = intent.getStringExtra("name")
                openProfile(birthId, name)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            //remove fragments from stack
            supportFragmentManager.popBackStackImmediate()

            DetailActivityViewModel.toolbarTitle
                    .removeAt(DetailActivityViewModel.toolbarTitle.size - 1)
            val temp = DetailActivityViewModel.toolbarTitle.last()
            titleTextView.text = temp
        } else {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        setResult(Activity.RESULT_OK, intentData)
    }


    private val intentData = Intent()
    private val TAG: String = javaClass.simpleName
    private lateinit var detailActivityViewModel: DetailActivityViewModel
}
