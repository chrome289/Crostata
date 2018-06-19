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
import xyz.siddharthseth.crostata.data.model.retrofit.Post
import xyz.siddharthseth.crostata.util.viewModel.BusyLoaderListener
import xyz.siddharthseth.crostata.viewmodel.activity.AddPostActivityViewModel
import java.io.File

class AddPostActivity : AppCompatActivity(), BusyLoaderListener {

    override var mutableNetStatusChanged = MutableLiveData<Boolean>()

    override fun isNetAvailable() {
    }

    override fun showError(isShown: Boolean) {
        if (isShown) {
            errorLayoutAddPost.visibility = View.VISIBLE
        } else {
            errorLayoutAddPost.visibility = View.GONE
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
            animationViewAddPost.setAnimation(R.raw.loader1)
            animationViewAddPost.scale = 0.2f
            animationViewAddPost.visibility = View.VISIBLE
            animationViewAddPost.playAnimation()
        } else {
            animationViewAddPost.cancelAnimation()
            animationViewAddPost.visibility = View.GONE
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

        //either show add image button or the selected image
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
            AddPostActivityViewModel.isImagePost = false
            AddPostActivityViewModel.imageUri = null
        }
        removeImage.visibility = if (AddPostActivityViewModel.imageUri == null) View.GONE else View.VISIBLE
        addImage.visibility = if (AddPostActivityViewModel.imageUri != null) View.GONE else View.VISIBLE
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
                                    Snackbar.make(theParent, Post.MESSAGE_SUBMITTED, Snackbar.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Snackbar.make(theParent, Post.MESSAGE_NOT_SUBMITTED, Snackbar.LENGTH_SHORT).show()
                                }
                            }, {
                                setLoaderVisibility(false, false, false)
                                Snackbar.make(theParent, Post.MESSAGE_NOT_SUBMITTED, Snackbar.LENGTH_SHORT).show()
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

        //copy selected image in cache and set uri and file
        if (requestCode == 1 && data != null) {
            addImage.visibility = View.GONE
            removeImage.visibility = View.VISIBLE

            AddPostActivityViewModel.isImagePost = true
            val file = File(cacheDir, "image.jpg")
            file.createNewFile()
            val outputStream = file.outputStream()
            val stream = contentResolver.openInputStream(data.data)
            stream.copyTo(outputStream)
            stream.close()
            outputStream.close()
            AddPostActivityViewModel.imageFile = file
            AddPostActivityViewModel.imageUri = data.data

            loadContentImage()
        }
    }

    /**
     * load selected image
     */
    private fun loadContentImage() {
        if (AddPostActivityViewModel.imageUri != null) {
            GlideApp.with(this)
                    .load(AddPostActivityViewModel.imageUri)
                    .fitCenter()
                    .into(contentImage)
            contentImage.requestLayout()
        }
    }
}
