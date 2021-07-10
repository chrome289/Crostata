package xyz.siddharthseth.crostata.ui.view.activity

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import xyz.siddharthseth.crostata.databinding.ActivityProfileBinding
import xyz.siddharthseth.crostata.ui.viewmodel.activity.ProfileActivityViewModel
import javax.inject.Inject

class ProfileActivity : AppCompatActivity() {

    private val args: ProfileActivityArgs by navArgs()

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileActivityViewModel =
            ViewModelProvider(this, viewModelFactory)[ProfileActivityViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        binding.recyclerViewProfile.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProfile.adapter = profileActivityViewModel.postRecyclerViewAdapter
        profileActivityViewModel.getProfile(args.userId)
    }

    @Inject
    lateinit var app: Application

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var profileActivityViewModel: ProfileActivityViewModel
}