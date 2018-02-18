package xyz.siddharthseth.crostata.view.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.model.LoginActivityViewModel


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"
    var loginActivityViewModel = LoginActivityViewModel()
    var compositeSubscription = CompositeSubscription()

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.signIn -> {
                    Log.v(TAG, birthId.text.toString())
                    showLoadingDialog()
                    compositeSubscription.add(loginActivityViewModel.signIn(birthId.text.toString(), password.text.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<Boolean> {
                                override fun onCompleted() {
                                    Log.d(TAG, "signIn->OnCompleted")
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }

                                override fun onNext(isSignInSuccessful: Boolean) {
                                    hideLoadingDialog()
                                    Log.d(TAG, "signIn->onNext - isSignInSuccessful=" + isSignInSuccessful)
                                    if (isSignInSuccessful)
                                        openHomePage()
                                    else
                                        showErrorAlert()
                                }
                            }))
                }
            }
        }
    }

    private fun hideLoadingDialog() {
    }

    private fun showLoadingDialog() {
    }

    private fun openHomePage() {
    }

    private fun showErrorAlert() {
    }

    override fun onDetachedFromWindow() {
        compositeSubscription.unsubscribe()
        super.onDetachedFromWindow()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginActivityViewModel = LoginActivityViewModel()
    }

    override fun onStart() {
        super.onStart()

        becomeCitizen.movementMethod = LinkMovementMethod.getInstance()
        signIn.setOnClickListener(this)
    }
}
