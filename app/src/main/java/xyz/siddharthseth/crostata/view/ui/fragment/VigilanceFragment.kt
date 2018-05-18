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

class VigilanceFragment : Fragment(), BusyLoaderListener {
    override fun showError(isShown: Boolean) {
        if (isShown) {
            errorLayout.visibility = View.VISIBLE
        } else {
            errorLayout.visibility = View.GONE
        }
    }

    override fun showLoader(isShown: Boolean) {
        if (isShown) {
            loadingFrame.visibility = View.VISIBLE
        } else {
            loadingFrame.visibility = View.GONE
        }
    }

    override fun showAnimation(isShown: Boolean) {
        if (isShown) {
            animationView.setAnimation(R.raw.loader1)
            animationView.scale = 0.2f
            animationView.visibility = View.VISIBLE
            animationView.playAnimation()
        } else {
            animationView.cancelAnimation()
            animationView.visibility = View.GONE
        }
    }

    private var listener: OnFragmentInteractionListener? = null
    lateinit var vigilanceViewModel: VigilanceViewModel

    private val observerSubject: Observer<Subject> = Observer {
        if (it != null)
            initSubjectInfo(it)
    }
    private val observerShowError: Observer<Boolean> = Observer {
        if (it != null) {
            showError(it)
        }
    }


    private val observerShowAnimation: Observer<Boolean> = Observer {
        if (it != null) {
            showAnimation(it)
        }
    }

    private val observerShowLoader: Observer<Boolean> = Observer {
        if (it != null) {
            showLoader(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        vigilanceViewModel = ViewModelProviders.of(this).get(VigilanceViewModel::class.java)

        vigilanceViewModel.mutableSubject.observe(this, observerSubject)
        vigilanceViewModel.mutableShowAnimation.observe(this, observerShowAnimation)
        vigilanceViewModel.mutableShowError.observe(this, observerShowError)
        vigilanceViewModel.mutableShowLoader.observe(this, observerShowLoader)
        vigilanceViewModel.init()
    }

    override fun onStop() {
        super.onStop()
        vigilanceViewModel.mutableSubject.removeObserver(observerSubject)
        vigilanceViewModel.mutableShowAnimation.removeObserver(observerShowAnimation)
        vigilanceViewModel.mutableShowError.removeObserver(observerShowError)
        vigilanceViewModel.mutableShowLoader.removeObserver(observerShowLoader)
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

    private fun setLoaderVisibility(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
        showLoader(isLoaderVisible)
        showAnimation(isAnimationVisible)
        showError(isErrorVisible)
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
