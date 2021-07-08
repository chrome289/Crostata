package xyz.siddharthseth.crostata.ui.viewmodel.activity

import android.Manifest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MainActivityViewModel
@Inject constructor() : ViewModel() {
//
//    fun startSignUp() {
//        mutableLoginStatus.value = SIGNUP_STARTED
//    }
//
//    fun performLogin() {
//        mutableLoginStatus.value = LOGIN_STARTED
//        performLoginUseCase.execute()
//                /* .flatMap {
//                     getCitizenUseCase.execute(it)
//                 }*/
//                .subscribe(loginObserver)
//    }

    var mutableLoginStatus = MutableLiveData<Int>()
    var getCitizenObservable = PublishSubject.create<Boolean>()

    var loginDisposable: Disposable? = null
    private val loginObserver = object : SingleObserver<String> {
        override fun onSuccess(t: String) {
            mutableLoginStatus.value = LOGIN_COMPLETED
            loginDisposable?.dispose()
        }

        override fun onSubscribe(d: Disposable) {
            loginDisposable = d
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
            mutableLoginStatus.value = LOGIN_FAILED

            loginDisposable?.dispose()
        }
    }

//
//    var getCitizenDisposable: Disposable? = null
//    private val getCitizenObserver = object : SingleObserver<Citizen> {
//        override fun onSuccess(t: Citizen) {
//            mutableLoginStatus.value = LOGIN_COMPLETED
//            getCitizenDisposable?.dispose()
//        }
//
//        override fun onSubscribe(d: Disposable) {
//            getCitizenDisposable = d
//        }
//
//        override fun onError(e: Throwable) {
//            e.printStackTrace()
//            mutableLoginStatus.value = LOGIN_FAILED
//
//            getCitizenDisposable?.dispose()
//        }
//    }


    fun syncData() {
        /* if (mutableDataSyncStatus.value != DataSyncStatus.ONGOING) {
             dataSyncUseCase.execute()
                     .subscribe(dataSyncObserver)
         }*/
    }

    fun toggleBottomNavigation(b: Boolean) {
        mutableBottomNavigation.value = b
    }

    val mutableDataSyncStatus = MutableLiveData<Int>()
    val mutableBottomNavigation = MutableLiveData<Boolean>()

    private var dataSyncDisposable: Disposable? = null
    private var dataSyncObserver = object : CompletableObserver {
        override fun onComplete() {
            //     mutableDataSyncStatus.value = DataSyncStatus.COMPLETED
            dataSyncDisposable?.dispose()
        }

        override fun onSubscribe(d: Disposable) {
            dataSyncDisposable = d
            //  mutableDataSyncStatus.value = DataSyncStatus.ONGOING
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
            //   mutableDataSyncStatus.value = DataSyncStatus.FAILED
            dataSyncDisposable?.dispose()
        }
    }


    companion object {
        val TAG = MainActivityViewModel::class.java.simpleName
        const val PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

        const val REQUEST_CODE_PERMISSIONS = 10
        const val REQUEST_CODE_GOOGLE_SIGN_IN = 11

        const val LOGIN_COMPLETED = 20
        const val LOGIN_STARTED = 21
        const val LOGIN_FAILED = 22
        const val LOGIN_NOT_ATTEMPTED = 23

        const val SIGNUP_STARTED = 30
        const val SIGNUP_COMPLETED = 31
        const val SIGNUP_ABORTED = 32
    }
}
