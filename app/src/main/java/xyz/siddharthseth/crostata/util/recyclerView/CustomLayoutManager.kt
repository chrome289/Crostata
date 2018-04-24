package xyz.siddharthseth.crostata.util.recyclerView

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class CustomLayoutManager : LinearLayoutManager {
    private var extraLayoutSpace = -1
    private var context: Context? = null

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, extraLayoutSpace: Int) : super(context) {
        this.context = context
        this.extraLayoutSpace = extraLayoutSpace
    }

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {
        this.context = context
    }

    fun setExtraLayoutSpace(extraLayoutSpace: Int) {
        this.extraLayoutSpace = extraLayoutSpace
    }

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return if (extraLayoutSpace > 0) {
            extraLayoutSpace
        } else DEFAULT_EXTRA_LAYOUT_SPACE
    }

    companion object {
        private const val DEFAULT_EXTRA_LAYOUT_SPACE = 600
    }
}