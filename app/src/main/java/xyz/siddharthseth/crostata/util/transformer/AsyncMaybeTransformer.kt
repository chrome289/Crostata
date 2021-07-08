package xyz.siddharthseth.crostata.util.transformer

import io.reactivex.MaybeTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AsyncMaybeTransformer {
    companion object {
        fun <T> applyIoScheduler(): MaybeTransformer<T, T> {
            return MaybeTransformer {
                it.subscribeOn(Schedulers.io())
            }
        }

        fun <T> applyUiScheduler(): MaybeTransformer<T, T> {
            return MaybeTransformer {
                it.observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}