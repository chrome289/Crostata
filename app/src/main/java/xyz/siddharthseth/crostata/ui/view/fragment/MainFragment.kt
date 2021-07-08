package xyz.siddharthseth.crostata.ui.view.fragment

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.ui.viewmodel.activity.MainActivityViewModel
import javax.inject.Inject

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return null// inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MainActivityViewModel::class.java)

    }

    override fun onResume() {
        super.onResume()
        findNavController(this@MainFragment)
            .navigate(R.id.action_mainFragment_to_homeFragment)
//
//        mainActivityViewModel.performLogin()
//        mainActivityViewModel.mutableLoginStatus.observe(this, loginStatusObserver)
    }

    @Inject
    lateinit var app: Application

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var loginStatusDisposable: Disposable? = null
    private var loginStatusObserver = Observer<Int> { t ->
        when (t) {
            MainActivityViewModel.LOGIN_NOT_ATTEMPTED -> {
                //  mainActivityViewModel.toggleBottomNavigation(false)
                // mainActivityViewModel.performLogin()
            }
            MainActivityViewModel.LOGIN_FAILED -> {
                //    mainActivityViewModel.toggleBottomNavigation(false)
                //       mainActivityViewModel.startSignUp()
            }
            MainActivityViewModel.SIGNUP_STARTED -> {
                //    mainActivityViewModel.toggleBottomNavigation(false)
                findNavController(this@MainFragment)
                    .navigate(R.id.action_mainFragment_to_loginFragment)
            }
            MainActivityViewModel.LOGIN_STARTED -> {
                //show progress
            }
            MainActivityViewModel.LOGIN_COMPLETED -> {
                //   mainActivityViewModel.toggleBottomNavigation(true)
                findNavController(this@MainFragment)
                    .navigate(R.id.action_mainFragment_to_homeFragment)
            }
        }
    }
}