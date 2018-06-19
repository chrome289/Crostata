package xyz.siddharthseth.crostata.util.recyclerView

import android.content.res.ColorStateList

/**
 * listener interface for vigilance reports
 */
interface VigilanceReportListener {
    /**
     * positive report color
     */
    val positiveColorTint: ColorStateList

    /**
     * negative report color
     */
    val negativeColorTint: ColorStateList
}