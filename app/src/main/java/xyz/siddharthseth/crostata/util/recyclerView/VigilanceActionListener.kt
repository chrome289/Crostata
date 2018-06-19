package xyz.siddharthseth.crostata.util.recyclerView

import android.content.res.ColorStateList

/**
 * listener interface for vigilance actions
 */
interface VigilanceActionListener {
    /**
     * positive action color
     */
    val positiveColorTint: ColorStateList

    /**
     * negative action color
     */
    val negativeColorTint: ColorStateList
}