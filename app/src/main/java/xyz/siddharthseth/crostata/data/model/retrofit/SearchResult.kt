package xyz.siddharthseth.crostata.data.model.retrofit

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

class SearchResult : Comparable<SearchResult> {
    class SearchResultGlove {
        var requestId: String = ""
        var list: List<SearchResult> = ArrayList()
    }

    override fun compareTo(other: SearchResult): Int {
        return this.name.compareTo(other.name)
    }

    private fun setGlideUrlProfileThumb(baseUrl: String, dimen: Int, quality: Int, token: String) {
        glideUrlProfileThumb = GlideUrl(baseUrl +
                "subject/profileImage?birthId=$birthId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())
    }

    fun initExtraInfo(baseUrl: String, token: String) {
        setGlideUrlProfileThumb(baseUrl, 128, 90, token)
    }

    var birthId: String = ""
    var name: String = ""
    var profession: String = ""
    lateinit var glideUrlProfileThumb: GlideUrl
}
