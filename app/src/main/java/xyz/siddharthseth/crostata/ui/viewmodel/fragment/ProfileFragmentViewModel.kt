package xyz.siddharthseth.crostata.ui.viewmodel.fragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao
import xyz.siddharthseth.crostata.data.dao.remote.ApiManager
import xyz.siddharthseth.crostata.domain.model.remote.FeedPost
import xyz.siddharthseth.crostata.domain.usecase.profile.GetProfileUseCase
import xyz.siddharthseth.crostata.ui.adapter.FeedRecyclerViewAdapter
import xyz.siddharthseth.crostata.util.TimeConverter
import javax.inject.Inject

class ProfileFragmentViewModel
@Inject constructor(
    app: Application,
    private val apiManager: ApiManager,
    timeConverter: TimeConverter,
    private val getProfileUseCase: GetProfileUseCase,
    sharedPreferencesDao: SharedPreferencesDao
) : ViewModel() {

    fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getProfileUseCase.execute("00912038-354d-434c-a8cb-051c21f09673")
            Log.v("taginga", user.email)
        }
    }

    var postRecyclerViewAdapter =
        FeedRecyclerViewAdapter(timeConverter, sharedPreferencesDao, object :
            DiffUtil.ItemCallback<FeedPost>() {
            override fun areContentsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
                return oldItem.postId == newItem.postId
            }
        })

    companion object {
        val TAG: String = ProfileFragmentViewModel::class.java.simpleName
    }

}