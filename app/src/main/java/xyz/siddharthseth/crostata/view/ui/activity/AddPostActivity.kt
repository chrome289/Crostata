package xyz.siddharthseth.crostata.view.ui.activity

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.activity_add_post.*
import rx.android.schedulers.AndroidSchedulers
import xyz.siddharthseth.crostata.R
import xyz.siddharthseth.crostata.data.model.LoggedSubject
import xyz.siddharthseth.crostata.data.model.glide.GlideApp
import xyz.siddharthseth.crostata.util.viewModel.BusyLoaderListener
import xyz.siddharthseth.crostata.viewmodel.activity.AddPostActivityViewModel
import java.io.File

class AddPostActivity : AppCompatActivity(), BusyLoaderListener {

    override var mutableNetStatusChanged = MutableLiveData<Boolean>()

    override fun isNetAvailable() {
    }

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

    override fun setLoaderVisibility(isLoaderVisible: Boolean, isAnimationVisible: Boolean, isErrorVisible: Boolean) {
        showLoader(isLoaderVisible)
        showAnimation(isAnimationVisible)
        showError(isErrorVisible)
    }

    private lateinit var addPostActivityViewModel: AddPostActivityViewModel
    val TAG: String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        addPostActivityViewModel = ViewModelProviders.of(this).get(AddPostActivityViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()

        addImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(intent, 1)
        }
        removeImage.setOnClickListener {
            addImage.visibility = View.VISIBLE
            removeImage.visibility = View.GONE
            GlideApp.with(this)
                    .load(R.drawable.home_feed_content_placeholder)
                    .into(contentImage)
            addPostActivityViewModel.isImagePost = false
            addPostActivityViewModel.imageUri = null
        }
        removeImage.visibility = if (addPostActivityViewModel.imageUri == null) View.GONE else View.VISIBLE
        addImage.visibility = if (addPostActivityViewModel.imageUri != null) View.GONE else View.VISIBLE
        setProfileDetails()
        loadContentImage()
    }

    private fun setProfileDetails() {
        profileName.text = LoggedSubject.name
        GlideApp.with(this)
                .load(getProfileImageLink())
                .priority(Priority.LOW)
                .placeholder(R.drawable.home_feed_content_placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fallback(R.drawable.home_feed_content_placeholder)
                .into(profileImage)
    }

    private fun getProfileImageLink(): GlideUrl {
        return GlideUrl(getString(R.string.server_url) +
                "subject/profileImage?birthId=${LoggedSubject.birthId}&dimen=320&quality=80"
                , LazyHeaders.Builder()
                .addHeader("authorization", addPostActivityViewModel.token)
                .build())
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_add_post, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                if (agreementBox.isChecked) {
                    setLoaderVisibility(true, true, false)
                    addPostActivityViewModel.submitPost(contentText.text.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                setLoaderVisibility(false, false, false)
                                if (it) {
                                    Snackbar.make(theParent, "Post submitted.", Snackbar.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Snackbar.make(theParent, "Post not submitted. Try Again.", Snackbar.LENGTH_SHORT).show()
                                }
                            }, {
                                setLoaderVisibility(false, false, false)
                                Snackbar.make(theParent, "Post not submitted. Try Again.", Snackbar.LENGTH_SHORT).show()
                                it.printStackTrace()
                            })
                } else {
                    Snackbar.make(theParent, "Check the checkBox before sharing.", Snackbar.LENGTH_SHORT).show()
                }
            }
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && data != null) {
            addImage.visibility = View.GONE
            removeImage.visibility = View.VISIBLE

            addPostActivityViewModel.isImagePost = true
            val file = File(cacheDir, "image.jpg")
            file.createNewFile()
            val outputStream = file.outputStream()
            val stream = contentResolver.openInputStream(data.data)
            stream.copyTo(outputStream)
            stream.close()
            outputStream.close()
            addPostActivityViewModel.imageFile = file
            addPostActivityViewModel.imageUri = data.data

            loadContentImage()
        }
    }

    private fun loadContentImage() {
        if (addPostActivityViewModel.imageUri != null) {
            GlideApp.with(this)
                    .load(addPostActivityViewModel.imageUri)
                    .fitCenter()
                    .into(contentImage)
            contentImage.requestLayout()
        }
    }
}
