package xyz.siddharthseth.crostata.ui.view.activity

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import xyz.siddharthseth.crostata.databinding.ActivityPostBinding
import xyz.siddharthseth.crostata.ui.viewmodel.activity.PostActivityViewModel
import javax.inject.Inject

class PostActivity : AppCompatActivity() {

    private val args: PostActivityArgs by navArgs()

    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postActivityViewModel =
            ViewModelProvider(this, viewModelFactory)[PostActivityViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()

        binding.recyclerViewComment.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewComment.adapter = postActivityViewModel.commentRecyclerViewAdapter
        postActivityViewModel.getPost(args.postId)
        binding.textView2.text = "Hallo"
    }

    @Inject
    lateinit var app: Application

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var postActivityViewModel: PostActivityViewModel
}