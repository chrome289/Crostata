package xyz.siddharthseth.crostata.ui.view.fragment

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.siddharthseth.crostata.databinding.FragmentHomeBinding
import xyz.siddharthseth.crostata.ui.viewmodel.fragment.HomeFragmentViewModel
import javax.inject.Inject

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFragmentViewModel =
            ViewModelProvider(this, viewModelFactory)[HomeFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

    override fun onResume() {
        super.onResume()
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewHome.adapter = homeFragmentViewModel.postRecyclerViewAdapter
//        GlideApp.with(this)
//                .load("https://via.placeholder.com/350x150")
//                .into(binding.imageViewProfileImage)
/*
        swipeRefreshLayoutHome.isRefreshing = false
        swipeRefreshLayoutHome.setOnRefreshListener {
            homeFragmentViewModel.syncPosts()
        }*/
        viewLifecycleOwner.lifecycleScope.launch {
            homeFragmentViewModel.syncPosts()
                .collect {
                    homeFragmentViewModel.postRecyclerViewAdapter.submitData(it)
                }
        }
    }

    @Inject
    lateinit var app: Application

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var homeFragmentViewModel: HomeFragmentViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

}
