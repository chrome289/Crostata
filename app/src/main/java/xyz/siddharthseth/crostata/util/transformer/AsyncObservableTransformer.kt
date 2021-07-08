package xyz.siddharthseth.crostata.util.transformer

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AsyncObservableTransformer {
    companion object {
        fun <T> applyIoScheduler(): ObservableTransformer<T, T> {
            return ObservableTransformer {
                it.subscribeOn(Schedulers.io())
            }
        }

        fun <T> applyUiScheduler(): ObservableTransformer<T, T> {
            return ObservableTransformer {
                it.observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}