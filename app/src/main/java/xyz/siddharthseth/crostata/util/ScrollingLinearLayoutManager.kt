package xyz.siddharthseth.crostata.util

import android.R.attr.duration
import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView

class ScrollingLinearLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean)
    : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?,
                                        position: Int) {
        val firstVisibleChild = recyclerView.getChildAt(0)
        val itemHeight = firstVisibleChild.height
        val currentPosition = recyclerView.getChildPosition(firstVisibleChild)
        var distanceInPixels = Math.abs((currentPosition - position) * itemHeight)
        if (distanceInPixels == 0) {
            distanceInPixels = Math.abs(firstVisibleChild.y).toInt()
        }
        val smoothScroller = SmoothScroller(recyclerView.context, distanceInPixels, duration)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private inner class SmoothScroller(context: Context, distanceInPixels: Int, duration: Int) : LinearSmoothScroller(context) {
        private val distanceInPixels: Float = distanceInPixels.toFloat()
        private val duration: Float
        private val TARGET_SEEK_SCROLL_DISTANCE_PX = 10000

        init {
            val millisecondsPerPx = calculateSpeedPerPixel(context.resources.displayMetrics)
            this.duration = (if (distanceInPixels < TARGET_SEEK_SCROLL_DISTANCE_PX)
                (Math.abs(distanceInPixels) * millisecondsPerPx).toInt()
            else
                duration).toFloat()
        }

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@ScrollingLinearLayoutManager
                    .computeScrollVectorForPosition(targetPosition)
        }

        override fun calculateTimeForScrolling(dx: Int): Int {
            val proportion = dx.toFloat() / distanceInPixels
            return (duration * proportion).toInt()
        }
    }
}