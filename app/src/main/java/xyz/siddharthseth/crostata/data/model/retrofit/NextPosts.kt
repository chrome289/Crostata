package xyz.siddharthseth.crostata.data.model.retrofit

import xyz.siddharthseth.crostata.data.model.Post

class NextPosts {
    var success = false
    var resultCode = 0
    var posts: List<Post> = ArrayList<Post>()
}