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
import xyz.siddharthseth.crostata.viewmodel.activity.LoginActivityViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG: String = javaClass.simpleName
    private lateinit var loginActivityViewModel: LoginActivityViewModel

    private var compositeSubscription = CompositeSubscription()

    override fun onClick(view: View) {
        when (view.id) {
            R.id.signIn -> {
                signIn()
            }
            R.id.helpButton -> {
                showHelp()
            }
        }
    }

    private fun hideLoadingDialog() {
        loadingFrame.visibility = View.GONE
        animationViewLogin.visibility = View.GONE
        animationViewLogin.cancelAnimation()
    }

    private fun showLoadingDialog() {
        loadingFrame.visibility = View.VISIBLE
        animationViewLogin.visibility = View.VISIBLE
        animationViewLogin.playAnimation()
    }

    private fun openHomePage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun getSubjectDetails() {
        loginActivityViewModel.getSubjectDetails(birthId.text.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loginActivityViewModel.subject = it
                    loginActivityViewModel.saveSubjectDetails(birthId.text.toString(), password.text.toString(), it.name)
                    hideLoadingDialog()
                    openHomePage()
                }, {
                    it.printStackTrace()
                })
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

    private fun showHelp() {

        val alertDialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setNegativeButton("CANCEL", { dialog, _ -> dialog.dismiss() })
                .setPositiveButton("AUTO-FILL", { dialog, _ ->
                    birthId.setText(getString(R.string.sample_birthid))
                    password.setText(getString(R.string.sample_password))
                    signIn()
                    dialog.dismiss()
                })
                .setTitle("Login details")
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
        animationViewLogin.setAnimation(R.raw.loader1)

        if (LoggedSubject.isInitDone()) {
            showLoadingDialog()
            autoFillFields()
        }
    }

    private fun autoFillFields() {
        //val user = SharedPreferencesService().getUserDetails(applicationContext)
        birthId.setText(LoggedSubject.birthId)
        password.setText(LoggedSubject.password)
    }

    private fun signIn() {
        showLoadingDialog()
        loginActivityViewModel.signIn(birthId.text.toString(), password.text.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resultCode ->
                    if (resultCode == 0) {
                        getSubjectDetails()
                    } else {
                        showErrorAlert(resultCode)
                        hideLoadingDialog()
                    }
                }, { onError ->
                    onError.printStackTrace()
                    hideLoadingDialog()
                    showErrorAlert(3)
                })
    }

    override fun onStart() {
        super.onStart()

        signIn.setOnClickListener(this)
        helpButton.setOnClickListener(this)
    }
}
