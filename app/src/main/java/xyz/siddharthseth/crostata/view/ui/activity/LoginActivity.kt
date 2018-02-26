package xyz.siddharthseth.crostata.view.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import rx.subscriptions.CompositeSubscription
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.modelView.LoginActivityViewModel


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"

    var loginActivityViewModel = LoginActivityViewModel()
    var compositeSubscription = CompositeSubscription()

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.signIn -> {
                    showLoadingDialog()
                    loginActivityViewModel.signIn(birthId.text.toString(), password.text.toString(), this)
                            .subscribe({ resultCode ->
                                hideLoadingDialog()
                                if (resultCode == 0) {
                                    openHomePage()
                                } else {
                                    showErrorAlert(resultCode)
                                }
                            }, { onError ->
                                Log.v(TAG, "Network Error")
                                hideLoadingDialog()
                                showErrorAlert(3)
                            })
                }
            }
        } else
            Log.v(TAG, "view is null")
    }

    private fun hideLoadingDialog() {
        loadingFrame.visibility = View.GONE
        animationView.cancelAnimation()
    }

    private fun showLoadingDialog() {
        loadingFrame.visibility = View.VISIBLE
        animationView.playAnimation()
    }

    private fun openHomePage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showErrorAlert(resultCode: Int) {
        var message = ""
        when (resultCode) {
            1 -> message = "No User with this Birth ID exists"
            2 -> message = "Incorrect password"
            else -> message = "Check your Internet Connection and try again"
        }

        val alertDialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton("OKAY", { dialog, which -> dialog.dismiss() })
                .setTitle("Login Unsuccessful")
                .setMessage(message)
                .create()

        alertDialog.show()
    }

    override fun onDetachedFromWindow() {
        compositeSubscription.unsubscribe()
        super.onDetachedFromWindow()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingFrame.setOnClickListener { v ->
            Log.v(TAG, "Don't click")
            true
        }

        val token = SharedPrefrencesService().getToken(this)
        if (token.isNotEmpty()) {
            showLoadingDialog()
            loginActivityViewModel.signInSilently(token, this)
                    .subscribe({ resultCode ->
                        hideLoadingDialog()
                        if (resultCode == 0) {
                            openHomePage()
                        } else {
                            showErrorAlert(resultCode)
                        }
                    }, { error ->
                        Log.v(TAG, "Network Error " + error.message)
                        hideLoadingDialog()
                        showErrorAlert(3)
                    })
        }
    }

    override fun onStart() {
        super.onStart()

        becomeCitizen.movementMethod = LinkMovementMethod.getInstance()
        signIn.setOnClickListener(this)
    }
}