package xyz.siddharthseth.crostata.view.ui.activity

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.viewmodel.activity.LoginActivityViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"
    private lateinit var loginActivityViewModel: LoginActivityViewModel

    private var compositeSubscription = CompositeSubscription()

    override fun onClick(view: View) {
        when (view.id) {
            R.id.signIn -> {
                showLoadingDialog()
                // Log.v(TAG, "here0")
                loginActivityViewModel.signIn(birthId.text.toString(), password.text.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ resultCode ->
                            if (resultCode == 0) {
                                openHomePage()
                                hideLoadingDialog()
                                finish()
                            } else {
                                showErrorAlert(resultCode)
                                hideLoadingDialog()
                            }
                        }, { onError ->
                            onError.printStackTrace()
                            Log.v(TAG, "Network Error")
                            hideLoadingDialog()
                            showErrorAlert(3)
                        })

            }
        }
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
    }

    private fun showErrorAlert(resultCode: Int) {
        val message: String = when (resultCode) {
            1 -> "No User with this Birth ID exists"
            2 -> "Incorrect password"
            else -> "Check your Internet Connection and try again"
        }

        val alertDialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton("OKAY", { dialog, _ -> dialog.dismiss() })
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

        loginActivityViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel::class.java)

        loadingFrame.setOnTouchListener { _, _ ->
            Log.v(TAG, "Don't click")
            true
        }
        animationView.setAnimation(R.raw.loader1)

        val token = SharedPreferencesService().getToken(applicationContext)
        if (token.isNotEmpty()) {
            showLoadingDialog()
            autoFillFields()
        }
    }

    private fun autoFillFields() {
        //val user = SharedPreferencesService().getUserDetails(applicationContext)
        birthId.setText(LoggedSubject.birthId)
        password.setText(LoggedSubject.password)
    }

    override fun onStart() {
        super.onStart()

        signIn.setOnClickListener(this)
    }
}
