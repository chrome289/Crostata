package xyz.siddharthseth.crostata.modelView

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import xyz.siddharthseth.crostata.data.model.Post

class ViewPostViewModel(application: Application) : AndroidViewModel(application) {
    var post: Post = Post()

    fun init() {

    }
}