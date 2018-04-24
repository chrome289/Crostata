package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.marlonlom.utilities.timeago.TimeAgo
import kotlinx.android.synthetic.main.recyclerview_comment_card.view.*
import xyz.siddharthseth.crostata.data.model.Comment
import xyz.siddharthseth.crostata.util.recyclerView.listeners.CommentItemListener
import xyz.siddharthseth.crostata.viewmodel.fragment.ViewPostViewModel
import java.text.SimpleDateFormat
import java.util.*


class CommentViewHolder(view: View, viewPostViewModel: ViewPostViewModel)
    : RecyclerView.ViewHolder(view) {

    private val TAG = javaClass.simpleName
    private var listener: CommentItemListener = viewPostViewModel
    private val calendar = Calendar.getInstance()
    private val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

    fun init(comment: Comment) {
        itemView.nameTextView.text = comment.name

        listener.loadProfileImage(comment.birthId, itemView.profileImage)

        itemView.textPost.text = comment.text

        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = inputFormat.parse(comment.timeCreated)

        itemView.timeTextView.text = TimeAgo.using(calendar.timeInMillis).toUpperCase()
    }
}
