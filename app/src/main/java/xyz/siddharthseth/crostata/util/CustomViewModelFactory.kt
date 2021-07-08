package xyz.siddharthseth.crostata.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xyz.siddharthseth.crostata.di.scope.AppScope
import javax.inject.Inject
import javax.inject.Provider

@Suppress("UNCHECKED_CAST")
@AppScope
class CustomViewModelFactory @Inject constructor(
    private val viewModelsMap: Map<Class<out ViewModel>,
            @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = viewModelsMap[modelClass]
        if (creator == null) {
            for (entry in viewModelsMap.entries) {
                if (modelClass.isAssignableFrom(entry.key)) {
                    creator = entry.value
                    break
                }
            }
        }
        requireNotNull(creator) { "Unknown model class $modelClass" }
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}