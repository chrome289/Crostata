package xyz.siddharthseth.crostata.ui.view.activity

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.android.AndroidInjection
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.databinding.ActivityMainBinding
import xyz.siddharthseth.crostata.ui.viewmodel.activity.MainActivityViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            intent.data?.path?.let {
                if (it.contains("/user/@")) {
                    val bundle = Bundle()
                    bundle.putString("userId", it.substring(it.indexOf("@") + 1))
                    findNavController(R.id.navHostFragment).navigate(
                        R.id.profileActivity,
                        bundle
                    )
                }
                if (it.contains("/post/@")) {
                    val bundle = Bundle()
                    bundle.putString("postId", it.substring(it.indexOf("@") + 1))
                    findNavController(R.id.navHostFragment).navigate(
                        R.id.postActivity, bundle
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        mainActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        /* if (mainActivityViewModel.isSessionTrackingActive()) {
             startActivity(Intent(this, SessionTrackingActivity::class.java))
         }*/

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.homeFragment) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }
        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.navHostFragment))
    }

    override fun onResume() {
        super.onResume()

        mainActivityViewModel.mutableBottomNavigation.observe(this, uiObserver)
    }

    override fun onPause() {
        super.onPause()
        mainActivityViewModel.mutableBottomNavigation.removeObserver(uiObserver)
    }


    private val uiObserver = Observer<Boolean> {
        binding.bottomNavigationView.visibility = if (it) View.VISIBLE else View.GONE
        if (it) {
            findNavController(R.id.navHostFragment).graph.startDestination = R.id.homeFragment
        }
    }

    @Inject
    lateinit var app: Application

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPreferencesDao: SharedPreferencesDao

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var binding: ActivityMainBinding

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }
}
