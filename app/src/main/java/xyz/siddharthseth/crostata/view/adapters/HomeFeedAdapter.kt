package xyz.siddharthseth.crostata.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.recyclerview_home_card.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.Glide.GlideApp
import xyz.siddharthseth.crostata.data.model.Post
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeFeedAdapter : RecyclerView.Adapter<AdapterPost>() {
    var postList = ArrayList<Post>()
    var token = ""

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AdapterPost {
        return AdapterPost(LayoutInflater.from(parent?.context).inflate(R.layout.recyclerview_home_card, parent, false)
                , parent!!.context, token)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: AdapterPost?, position: Int) {
        if (holder != null) {
            holder.init(postList.get(position))
        }
    }
}

class AdapterPost(view: View, private val context: Context, private val token: String) : RecyclerView.ViewHolder(view) {
    fun init(post: Post) {
        itemView.nameTextView.text = post.creatorName
        if (post.contentType == "TO") {
            itemView.imageView.visibility = View.GONE
        } else {
            itemView.imageView.visibility = View.VISIBLE

            //TODO shift this code to modelview
            val glideUrl = GlideUrl("http://192.168.1.123:3000" + "/api/content/getImagePost?post_id=" + post.postId
                    , LazyHeaders.Builder().addHeader("authorization", token).build())

            GlideApp.with(context)
                    .load(glideUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(itemView.imageView)

        }
        itemView.textPost.text = post.text
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        calendar.time = inputFormat.parse(post.timeCreated)

        val dateOutputFormat: DateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        val timeDateFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.US)

        itemView.dateTextView.text = dateOutputFormat.format(calendar.time)
        itemView.timeTextView.text = timeDateFormat.format(calendar.time)
    }
}