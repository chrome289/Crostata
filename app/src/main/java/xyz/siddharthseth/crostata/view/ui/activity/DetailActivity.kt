package xyz.siddharthseth.crostata.view.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.util.viewModel.ViewPostInteractionListener
import xyz.siddharthseth.crostata.view.ui.fragment.ProfileFragment
import xyz.siddharthseth.crostata.view.ui.fragment.ViewPostFragment

class DetailActivity : AppCompatActivity(), ViewPostInteractionListener {

    override fun openProfile(birthId: String) {
        val bundle = Bundle()
        if (LoggedSubject.birthId == birthId) {
            val intentData = Intent()
            intentData.putExtra("openItem", R.id.selfProfile)
            setResult(Activity.RESULT_OK, intentData)
            finish()
        } else {
            bundle.putString("birthId", birthId)
            profileFragment = ProfileFragment.newInstance(bundle)

            supportFragmentManager.beginTransaction()
                    .add(R.id.detailFrame, profileFragment)
                    .commit()
        }
    }

    override fun enableNavigationDrawer(isEnabled: Boolean) {

    }

    override fun showBackNavigationButton(isShown: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        post = intent.getParcelableExtra("post")
        viewPostFragment = ViewPostFragment.newInstance(post)

        supportFragmentManager.beginTransaction()
                .add(R.id.detailFrame, viewPostFragment)
                .commit()
    }


    private var viewPostFragment: ViewPostFragment? = null
    private var profileFragment: ProfileFragment? = null
    lateinit var post: Post

}
