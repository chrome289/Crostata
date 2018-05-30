package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_splash_actvity.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.viewmodel.activity.SplashActivityViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var splashActivityViewModel: SplashActivityViewModel
    val TAG: String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_actvity)

        animationView.setAnimation(R.raw.loader1)

        splashActivityViewModel = ViewModelProviders.of(this).get(SplashActivityViewModel::class.java)

        if (splashActivityViewModel.savedLoginDetailsAvailable()) {
            signInSilently()
        } else {
            openLoginActivity()
        }
    }

    private fun showErrorMessage() {
        errorLayout.visibility = View.VISIBLE
        refreshButton.setOnClickListener { signInSilently() }
    }

    private fun hideErrorMessage() {
        errorLayout.visibility = View.GONE
        refreshButton.setOnClickListener { }
    }

    private fun signInSilently() {
        showLoadingAnimation()
        hideErrorMessage()
        splashActivityViewModel.signInSilently()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        hideLoadingAnimation()
                        openHomePage()
                    } else {
                        signIn()
                    }
                }, {
                    it.printStackTrace()
                    hideLoadingAnimation()
                    showErrorMessage()
                })
    }

    private fun signIn() {
        splashActivityViewModel.signIn().subscribe({ resultCode ->
            if (resultCode == 0) {
                hideLoadingAnimation()
                openHomePage()
            } else {
                openLoginActivity()
            }
        }, { onError ->
            onError.printStackTrace()
            Log.v(TAG, "Network Error")
            hideLoadingAnimation()
            showErrorMessage()
        })
    }

    private fun hideLoadingAnimation() {
        animationView.visibility = View.GONE
        animationView.cancelAnimation()
    }

    private fun showLoadingAnimation() {
        animationView.visibility = View.VISIBLE
        animationView.playAnimation()
    }

    private fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun openHomePage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
