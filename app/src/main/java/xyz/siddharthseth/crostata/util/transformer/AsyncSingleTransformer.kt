package xyz.siddharthseth.crostata.util.transformer

import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AsyncSingleTransformer {
    companion object {
        fun <T> applyIoScheduler(): SingleTransformer<T, T> {
            return SingleTransformer {
                it.subscribeOn(Schedulers.io())
            }
        }

        fun <T> applyUiScheduler(): SingleTransformer<T, T> {
            return SingleTransformer {
                it.observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}