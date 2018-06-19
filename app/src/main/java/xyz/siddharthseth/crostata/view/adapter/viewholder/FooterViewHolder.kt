package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_loading_footer.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener

/**
 * viewholder for footer
 */
class FooterViewHolder(itemView: View?, private val footerListener: FooterListener) : RecyclerView.ViewHolder(itemView) {

    /**
     * init the comment item
     * @param secondLoaderVisible is secondary loader visible
     * @param errorVisible is error layout visible
     * @param endVisible is list end visible
     */
    fun init(secondLoaderVisible: Boolean, errorVisible: Boolean, endVisible: Boolean) {
        //secondary loader not visible
        if (!secondLoaderVisible && !errorVisible && !endVisible) {
            itemView.loadingFrame.visibility = View.GONE
        }
        //animation visible
        else if (secondLoaderVisible) {
            itemView.loadingFrame.visibility = View.VISIBLE
            itemView.loadingAnimationFooter.setAnimation(R.raw.loader1)
            itemView.loadingAnimationFooter.scale = 0.005f
            itemView.loadingAnimationFooter.visibility = View.VISIBLE
            itemView.loadingAnimationFooter.playAnimation()

            itemView.theEnd.visibility = View.GONE
            itemView.retryButton.visibility = View.GONE
        }
        //error visible
        else if (errorVisible) {
            itemView.loadingFrame.visibility = View.VISIBLE
            itemView.retryButton.visibility = View.VISIBLE

            itemView.theEnd.visibility = View.GONE
            itemView.loadingAnimationFooter.cancelAnimation()
            itemView.loadingAnimationFooter.visibility = View.GONE
        }
        //list end visible
        else {
            itemView.loadingFrame.visibility = View.VISIBLE
            itemView.theEnd.visibility = View.VISIBLE

            itemView.retryButton.visibility = View.GONE
            itemView.loadingAnimationFooter.cancelAnimation()
            itemView.loadingAnimationFooter.visibility = View.GONE
        }
        //net not available. show retry button
        itemView.retryButton.setOnClickListener {
            footerListener.reloadSecondary()
        }
    }
}