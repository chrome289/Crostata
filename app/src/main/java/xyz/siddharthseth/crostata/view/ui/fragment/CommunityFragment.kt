package xyz.siddharthseth.crostata.view.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.service.SharedPrefrencesService
import xyz.siddharthseth.crostata.view.adapter.ChartAdapter
import xyz.siddharthseth.crostata.viewmodel.fragment.CommunityViewModel

class CommunityFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var communityViewModel: CommunityViewModel
    var isInitialized = false
    lateinit var chartAdapter: ChartAdapter
    val sharedPreferencesService: SharedPrefrencesService = SharedPrefrencesService()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onResume() {
        super.onResume()

        /* if (!isInitialized || recyclerView.adapter.itemCount == 0) {

             communityViewModel = ViewModelProviders.of(this).get(CommunityViewModel::class.java)

             chartAdapter = ChartAdapter(communityViewModel)
             recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
             recyclerView.adapter = chartAdapter

             communityViewModel.getChart()
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe({ entry ->
                         chartAdapter.chartEntries.add(entry);
                     }, { err -> err.printStackTrace() })

             communityViewModel.getUserInfo()
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe({ subject ->
                         val text = getString(R.string.index_own_pre) + subject.patriotIndex.toString()
                         indexOwn.text = text

                         if (subject.patriotIndex <= 0)
                             info.visibility = View.VISIBLE
                         else
                             info.visibility = View.GONE

                         rankOwn.text = subject.rank.toString()
                         communityViewModel.loadProfileImage(subject.birthId, profileImageOwn, 512)

                     }, { err -> err.printStackTrace() })

             isInitialized = true
         }*/
    }

    interface OnFragmentInteractionListener {
    }

    companion object {
        fun newInstance(): CommunityFragment {
            val fragment = CommunityFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}