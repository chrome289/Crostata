package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_vigilance_action.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.VigilanceAction
import xyz.siddharthseth.crostata.util.recyclerView.VigilanceActionListener

/**
 * viewholder for vigilance action
 */
class VigilanceActionViewHolder(itemView: View, vigilanceActionListener: VigilanceActionListener) : RecyclerView.ViewHolder(itemView) {

    //colors
    private val positiveColor = vigilanceActionListener.positiveColorTint
    private val negativeColor = vigilanceActionListener.negativeColorTint

    /**
     * init vigilance action
     * @param vigilanceAction vigilance action
     */
    fun init(vigilanceAction: VigilanceAction) {
        itemView.actionPolarity.setImageResource(
                if (vigilanceAction.isPositive)
                    R.drawable.ic_action_add
                else
                    R.drawable.ic_action_minus
        )
        itemView.actionPolarity.imageTintList = if (vigilanceAction.isPositive) positiveColor else negativeColor
        itemView.actionDetail.text = vigilanceAction.action
    }
}