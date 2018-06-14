package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.SearchResult
import xyz.siddharthseth.crostata.util.recyclerView.FooterListener
import xyz.siddharthseth.crostata.util.recyclerView.SearchResultListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.FooterViewHolder
import xyz.siddharthseth.crostata.view.adapter.viewholder.SearchResultViewHolder

class SearchResultAdapter(private val searchResultListener: SearchResultListener, private val footerListener: FooterListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var searchResultList = ArrayList<SearchResult>()

    val TAG: String = javaClass.simpleName
    private val itemView = 1
    private val footerView = 2
    var isSecondLoaderVisible = false
    var isErrorVisible = false
    var isEndVisible: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemView) {
            SearchResultViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.recyclerview_subject_card, parent, false)
                    , searchResultListener
            )
        } else {
            FooterViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.recyclerview_loading_footer, parent, false)
                    , footerListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < searchResultList.size - 1)
            itemView
        else
            footerView
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchResultViewHolder) {
            holder.init(searchResultList[holder.adapterPosition])
            holder.loadImages(searchResultList[holder.adapterPosition])
        } else if (holder is FooterViewHolder) {
            holder.init(isSecondLoaderVisible, isErrorVisible, isEndVisible)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is SearchResultViewHolder) {
            holder.clearImages()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }
}