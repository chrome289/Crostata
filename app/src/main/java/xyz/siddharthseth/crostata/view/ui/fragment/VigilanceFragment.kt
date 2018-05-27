package xyz.siddharthseth.crostata.view.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_vigilance.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.retrofit.Subject
import xyz.siddharthseth.crostata.util.viewModel.BusyLoaderListener
import xyz.siddharthseth.crostata.viewmodel.fragment.VigilanceViewModel
import java.util.*

class VigilanceFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var vigilanceViewModel: VigilanceViewModel
    private var isInitialized = false

    private val observerSubject: Observer<Subject> = Observer {
        if (it != null)
            initSubjectInfo(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        vigilanceViewModel = ViewModelProviders.of(this).get(VigilanceViewModel::class.java)

        vigilanceViewModel.mutableSubject.observe(this, observerSubject)
        vigilanceViewModel.mutableLoaderConfig.observe(this, observerLoaderConfig)
        mListener!!.mutableNetStatusChanged.observe(this, observerNetStatus)
    }

    override fun onStop() {
        super.onStop()
        vigilanceViewModel.mutableSubject.removeObserver(observerSubject)
        vigilanceViewModel.mutableLoaderConfig.removeObserver(observerLoaderConfig)
        mListener!!.mutableNetStatusChanged.removeObserver(observerNetStatus)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vigilance, container, false)
    }

    override fun onResume() {
        super.onResume()

        vigilanceRecyclerViewAction.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        vigilanceRecyclerViewAction.adapter = vigilanceViewModel.vigilanceActionAdapter
        vigilanceRecyclerViewReport.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        vigilanceRecyclerViewReport.adapter = vigilanceViewModel.vigilanceReportAdapter

        if (!isInitialized) {
            vigilanceViewModel.init()
            isInitialized = true
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun initSubjectInfo(subject: Subject) {
        patriotIndex.text = String.format(Locale.US, getString(R.string.your_patriot_index_is_385), subject.patriotIndex)
        vigilanceViewModel.setVigilanceActions()
    }

    private val observerLoaderConfig: Observer<List<Boolean>> = Observer {
        if (it != null) {
            mListener!!.setLoaderVisibility(it[0], it[1], it[2])
        }
    }

    private val observerNetStatus: Observer<Boolean> = Observer {
        if (it != null) {
            if (it && vigilanceViewModel.isLoadPending) {
                mListener!!.setLoaderVisibility(true, true, false)
                vigilanceViewModel.refreshData()
            }
        }
    }

    interface OnFragmentInteractionListener : BusyLoaderListener

    companion object {
        @JvmStatic
        fun newInstance() =
                VigilanceFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}
