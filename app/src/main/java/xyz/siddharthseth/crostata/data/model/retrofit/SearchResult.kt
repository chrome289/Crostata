package xyz.siddharthseth.crostata.data.model.retrofit

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

class SearchResult : Comparable<SearchResult>, Cloneable {

    //cover class for getting requestid from the list
    class SearchResultGlove {
        var requestId: String = ""
        var list: List<SearchResult> = ArrayList()
    }

    //cloneable
    override fun clone(): SearchResult {
        return super.clone() as SearchResult
    }

    override fun compareTo(other: SearchResult): Int {
        return this.name.compareTo(other.name)
    }

    //equals implementation, using only birthId
    override fun equals(other: Any?): Boolean {
        other as SearchResult
        return this.birthId == other.birthId
    }

    //hashcode implementation
    override fun hashCode(): Int {
        var result = birthId.hashCode()
        result += 31 * name.hashCode()

        return result
    }

    //set glide url attribute for profile image thumb
    private fun setGlideUrlProfileThumb(baseUrl: String, dimen: Int, quality: Int, token: String) {
        glideUrlProfileThumb = GlideUrl(baseUrl +
                "subject/profileImage?birthId=$birthId&dimen=$dimen&quality=$quality"
                , LazyHeaders.Builder()
                .addHeader("authorization", token)
                .build())
    }

    //init extra attributes
    fun initExtraInfo(baseUrl: String, token: String) {
        setGlideUrlProfileThumb(baseUrl, 128, 90, token)
    }

    var birthId: String = ""
    var name: String = ""
    var profession: String = ""

    //generated attributes
    lateinit var glideUrlProfileThumb: GlideUrl

    companion object {

        //deep copy list
        fun cloneList(searchResultList: ArrayList<SearchResult>): ArrayList<SearchResult> {
            val newList = ArrayList<SearchResult>()
            for (searchResult in searchResultList) {
                newList.add(searchResult.clone())
            }
            return newList
        }
    }
}
