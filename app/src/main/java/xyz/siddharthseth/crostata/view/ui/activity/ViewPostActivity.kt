package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.activity_view_post.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.view.adapter.ViewPostCommentAdapter
import xyz.siddharthseth.crostata.viewmodel.activity.ViewPostActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

class ViewPostActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    private lateinit var commentSubscription: Subscription
    private lateinit var adapter: ViewPostCommentAdapter

    private lateinit var viewPostActivityViewModel: ViewPostActivityViewModel
    val TAG: String? = javaClass.simpleName

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        viewPostActivityViewModel = ViewModelProviders.of(this).get(ViewPostActivityViewModel::class.java)
        viewPostActivityViewModel.post = intent.extras.getParcelable("post")

        initializeView()
    }

    private fun initializeView() {

        toolbar.setNavigationOnClickListener { finish() }

        nameTextView.text = viewPostActivityViewModel.post.creatorName.toUpperCase()

        if (viewPostActivityViewModel.post.contentType == "TO") {
            imageView.visibility = View.GONE
            viewPostActivityViewModel.clearPostedImageGlide(imageView)
        } else {
            imageView.visibility = View.VISIBLE

            Log.v(TAG, "imageId-" + viewPostActivityViewModel.post.imageId)
            viewPostActivityViewModel.loadPostedImage(imageView)
            imageView.requestLayout()
        }

        viewPostActivityViewModel.loadProfileImage(viewPostActivityViewModel.post.creatorId, profileImage)

        textPost.text = viewPostActivityViewModel.post.text

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = inputFormat.parse(viewPostActivityViewModel.post.timeCreated)

        timeTextView.text = TimeAgo.using(calendar.timeInMillis).toUpperCase()


        adapter = ViewPostCommentAdapter(viewPostActivityViewModel)
        adapter.setHasStableIds(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        commentSubscription = viewPostActivityViewModel.getComments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ comment: Comment ->
                    adapter.commentList.add(comment)
                    setCommentTextView()
                    adapter.notifyItemInserted(adapter.commentList.size - 1)
                }, { error -> error.printStackTrace() })

        setCommentTextView()
    }

    private fun setCommentTextView() {
        commentHeader.text = if (adapter.commentList.size > 0) "Comments" else "No Comments"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

        // overridePendingTransition(0,R.anim.slide_down)
    }


}
