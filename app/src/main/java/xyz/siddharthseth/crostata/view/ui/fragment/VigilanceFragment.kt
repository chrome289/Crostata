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
import xyz.siddharthseth.crostata.viewmodel.fragment.VigilanceViewModel
import java.util.*

class VigilanceFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    lateinit var vigilanceViewModel: VigilanceViewModel
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
    }

    override fun onStop() {
        super.onStop()
        vigilanceViewModel.mutableSubject.removeObserver(observerSubject)
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

        vigilanceViewModel.getSubjectInfo()
        vigilanceViewModel.getReports()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initSubjectInfo(subject: Subject) {
        patriotIndex.text = String.format(Locale.US, getString(R.string.your_patriot_index_is_385), subject.patriotIndex)
        vigilanceViewModel.setVigilanceActions()
    }

    interface OnFragmentInteractionListener

    companion object {

        @JvmStatic
        fun newInstance() =
                VigilanceFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}
