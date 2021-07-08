package xyz.siddharthseth.crostata.util.transformer

import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AsyncFlowableTransformer {
    companion object {
        fun <T> applyIoScheduler(): FlowableTransformer<T, T> {
            return FlowableTransformer {
                it.subscribeOn(Schedulers.io())
            }
        }

        fun <T> applyUiScheduler(): FlowableTransformer<T, T> {
            return FlowableTransformer {
                it.observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}