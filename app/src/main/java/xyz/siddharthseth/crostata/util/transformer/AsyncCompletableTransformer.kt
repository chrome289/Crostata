package xyz.siddharthseth.crostata.util.transformer

import io.reactivex.CompletableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AsyncCompletableTransformer {
    companion object {
        fun applyIoScheduler(): CompletableTransformer {
            return CompletableTransformer {
                it.subscribeOn(Schedulers.io())
            }
        }

        fun applyUiScheduler(): CompletableTransformer {
            return CompletableTransformer {
                it.observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}