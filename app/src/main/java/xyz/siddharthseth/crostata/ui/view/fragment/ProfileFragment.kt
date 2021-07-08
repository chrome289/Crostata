package xyz.siddharthseth.crostata.ui.view.fragment

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import xyz.siddharthseth.crostata.databinding.FragmentProfileBinding
import xyz.siddharthseth.crostata.ui.viewmodel.fragment.ProfileFragmentViewModel
import javax.inject.Inject

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileFragmentViewModel =
            ViewModelProvider(this, viewModelFactory)[ProfileFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        profileFragmentViewModel.getProfile()
    }

    @Inject
    lateinit var app: Application

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var profileFragmentViewModel: ProfileFragmentViewModel
    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}