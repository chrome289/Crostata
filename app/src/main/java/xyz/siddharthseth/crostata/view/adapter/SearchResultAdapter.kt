package xyz.siddharthseth.crostata.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.SearchResult
import xyz.siddharthseth.crostata.util.recyclerView.SearchResultListener
import xyz.siddharthseth.crostata.view.adapter.viewholder.SearchResultViewHolder

class SearchResultAdapter(private val searchResultListener: SearchResultListener) : RecyclerView.Adapter<SearchResultViewHolder>() {
    var searchResultList = ArrayList<SearchResult>()
    val TAG: String = javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.recyclerview_subject_card, parent, false)
                , searchResultListener
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.init(searchResultList[holder.adapterPosition])
        holder.loadImages(searchResultList[holder.adapterPosition])
    }

    override fun onViewRecycled(holder: SearchResultViewHolder) {
        super.onViewRecycled(holder)
        holder.clearImages()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }
}