package xyz.siddharthseth.crostata.viewmodel.fragment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.VigilanceAction
import xyz.siddharthseth.crostata.data.model.retrofit.Report
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.data.providers.ContentRepositoryProvider
import xyz.siddharthseth.crostata.data.service.SharedPreferencesService
import xyz.siddharthseth.crostata.util.CurrencyFormatter
import xyz.siddharthseth.crostata.util.diffUtil.VigilanceActionDiffUtilCallback
import xyz.siddharthseth.crostata.util.diffUtil.VigilanceReportDiffUtilCallback
import xyz.siddharthseth.crostata.util.recyclerView.VigilanceActionListener
import xyz.siddharthseth.crostata.util.recyclerView.VigilanceReportListener
import xyz.siddharthseth.crostata.view.adapter.VigilanceActionAdapter
import xyz.siddharthseth.crostata.view.adapter.VigilanceReportAdapter
import java.util.*

class VigilanceViewModel(application: Application) : AndroidViewModel(application), VigilanceActionListener, VigilanceReportListener {

    override val positiveColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.green))
    override val negativeColorTint: ColorStateList
        get() = ColorStateList.valueOf(ContextCompat.getColor(getApplication(), R.color.colorPrimary))

    var mutableSubject = MutableLiveData<Subject>()
    var mutableShowAnimation = MutableLiveData<Boolean>()
    var mutableShowError = MutableLiveData<Boolean>()
    var mutableShowLoader = MutableLiveData<Boolean>()

    val contentRepository = ContentRepositoryProvider.getContentRepository()
    val sharedPreferencesService = SharedPreferencesService()
    var token = sharedPreferencesService.getToken(getApplication())
    var actionList = ArrayList<VigilanceAction>()
    val vigilanceActionAdapter = VigilanceActionAdapter(this)

    var reportList = ArrayList<Report>()
    val vigilanceReportAdapter = VigilanceReportAdapter(this)

    internal fun getSubjectInfo() {
        contentRepository.getSubjectInfo(token, LoggedSubject.birthId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mutableSubject.value = it
                }, {
                    it.printStackTrace()
                })
    }

    fun setVigilanceActions() {
        val subject: Subject = mutableSubject.value!!

        when (subject.profession) {
            "REBEL" -> {
                val vigilanceAction =
                        VigilanceAction(1,
                                false,
                                "Enemy of President's Regime.")
                actionList.add(vigilanceAction)
            }
            "NONE" -> {
                val vigilanceAction =
                        VigilanceAction(1,
                                false,
                                "Refused to contribute to Palmyra's Economy")
                actionList.add(vigilanceAction)
            }
            "SOLDIER" -> {
                val vigilanceAction =
                        VigilanceAction(1,
                                true,
                                "Fought for President's Regime.")
                actionList.add(vigilanceAction)
            }
            "PEASANT" -> {
                val vigilanceAction =
                        VigilanceAction(1,
                                true,
                                "Following President's Orders.")
                actionList.add(vigilanceAction)
            }
            "MERCHANT" -> {
                val vigilanceAction =
                        VigilanceAction(1,
                                true,
                                "Helping grow Palmyrene Economy.")
                actionList.add(vigilanceAction)
            }
            "OLIGARCH" -> {
                val vigilanceAction =
                        VigilanceAction(1,
                                true,
                                "Member of President's Advisory Council.")
                actionList.add(vigilanceAction)
            }
        }

        if (subject.informer) {
            val vigilanceAction =
                    VigilanceAction(2,
                            true,
                            "Takes part in President's Hyperlocal Surveillance Programme.")
            actionList.add(vigilanceAction)
        }
        if (subject.moneyDonated > 0) {
            val vigilanceAction =
                    VigilanceAction(3,
                            true,
                            "Donated " + CurrencyFormatter.commaSeparated(subject.moneyDonated) + " to President's Fund.")
            actionList.add(vigilanceAction)
        }
        if (subject.reportsMade > 0) {
            val vigilanceAction =
                    VigilanceAction(4,
                            true,
                            "Successfully reported " + subject.reportsMade + " unpatriotic Citizens.")
            actionList.add(vigilanceAction)
        }
        //Todo >= for testing
        if (subject.reportsAgainst >= 0) {
            val vigilanceAction =
                    VigilanceAction(5,
                            false,
                            "Involved in " + subject.reportsAgainst + " unpatriotic activities.")
            actionList.add(vigilanceAction)
        }

        updateVigilanceActionAdapter()
    }

    private fun updateVigilanceActionAdapter() {
        val diffUtil = DiffUtil.calculateDiff(
                VigilanceActionDiffUtilCallback(vigilanceActionAdapter.actionReportList, actionList))
        vigilanceActionAdapter.actionReportList.clear()
        vigilanceActionAdapter.actionReportList.addAll(actionList)
        // lastTimestamp = postList[postList.size - 1].getTimestamp()
        //  isLoading = false
        diffUtil.dispatchUpdatesTo(vigilanceActionAdapter)

        //setLoaderLiveData(false, false, false)
    }

    private fun updateVigilanceReportAdapter() {
        reportList.sort()
        val diffUtil = DiffUtil.calculateDiff(
                VigilanceReportDiffUtilCallback(vigilanceReportAdapter.reportList, reportList))
        vigilanceReportAdapter.reportList.clear()
        vigilanceReportAdapter.reportList.addAll(reportList)
        // lastTimestamp = postList[postList.size - 1].getTimestamp()
        //  isLoading = false
        diffUtil.dispatchUpdatesTo(vigilanceReportAdapter)

        setLoaderLiveData(false, false, false)
    }

    fun getReports() {
        contentRepository.getReports(token, LoggedSubject.birthId)
                .subscribeOn(Schedulers.io())
                .flatMap { Observable.from(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.initExtraInfo()
                    reportList.add(it)
                }, {
                    setLoaderLiveData(true, false, true)
                    it.printStackTrace()
                }, {

                    updateVigilanceReportAdapter()
                })
    }

    private fun setLoaderLiveData(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
        mutableShowLoader.value = isLoaderVisible
        mutableShowAnimation.value = isAnimationVisible
        mutableShowError.value = isErrorVisible
    }

    fun init() {
        setLoaderLiveData(true, true, false)
        getSubjectInfo()
        getReports()
    }
}
