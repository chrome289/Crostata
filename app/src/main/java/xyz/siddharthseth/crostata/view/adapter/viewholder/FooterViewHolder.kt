package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_loading_footer.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener

class FooterViewHolder(itemView: View?, private val footerListener: FooterListener) : RecyclerView.ViewHolder(itemView) {

    fun init(secondLoaderVisible: Boolean, errorVisible: Boolean, endVisible: Boolean) {
        if (!secondLoaderVisible && !errorVisible && !endVisible) {
            itemView.loadingFrame.visibility = View.GONE
        } else if (secondLoaderVisible) {
            itemView.loadingFrame.visibility = View.VISIBLE
            itemView.loadingAnimationFooter.setAnimation(R.raw.loader1)
            itemView.loadingAnimationFooter.scale = 0.005f
            itemView.loadingAnimationFooter.visibility = View.VISIBLE
            itemView.loadingAnimationFooter.playAnimation()

            itemView.theEnd.visibility = View.GONE
            itemView.retryButton.visibility = View.GONE
        } else if (errorVisible) {
            itemView.loadingFrame.visibility = View.VISIBLE
            itemView.retryButton.visibility = View.VISIBLE

            itemView.theEnd.visibility = View.GONE
            itemView.loadingAnimationFooter.cancelAnimation()
            itemView.loadingAnimationFooter.visibility = View.GONE
        } else {
            itemView.loadingFrame.visibility = View.VISIBLE
            itemView.theEnd.visibility = View.VISIBLE

            itemView.retryButton.visibility = View.GONE
            itemView.loadingAnimationFooter.cancelAnimation()
            itemView.loadingAnimationFooter.visibility = View.GONE
        }
        itemView.retryButton.setOnClickListener {
            footerListener.reloadSecondary()
        }
    }
}