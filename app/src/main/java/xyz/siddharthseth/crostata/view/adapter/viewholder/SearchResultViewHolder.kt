package xyz.siddharthseth.crostata.view.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.recyclerview_subject_card.view.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.SearchResult
import xyz.siddharthseth.crostata.util.recyclerView.SearchResultListener

class SearchResultViewHolder(view: View, private val searchResultListener: SearchResultListener)
    : RecyclerView.ViewHolder(view), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.profileImage, R.id.profileName, R.id.profession, R.id.subjectContainer -> {
                searchResultListener.openProfile(adapterPosition)
            }
        }
    }

    fun init(searchResult: SearchResult) {
        itemView.profileName.text = searchResult.name
        itemView.profileName.setOnClickListener(this)
        itemView.profession.text = searchResult.profession.toLowerCase().capitalize()
        itemView.profession.setOnClickListener(this)
        itemView.subjectContainer.setOnClickListener(this)
    }

    fun clearImages() {
        searchResultListener.clearImage(itemView.profileImage)
    }

    fun loadImages(searchResult: SearchResult) {
        searchResultListener.loadProfileImage(searchResult, 128, itemView.profileImage)
        itemView.profileImage.setOnClickListener(this)
    }
}