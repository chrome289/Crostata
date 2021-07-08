package xyz.siddharthseth.crostata.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "users")
@JsonClass(generateAdapter = true)
data class User(
    @PrimaryKey
    val userId: String = "",
    val email: String = "",
    var username: String = "",
    var name: String = "",
    var followerCount: String = "",
    var dateOfBirth: String = "",

    @Json(name = "imageId")
    var profileImageUrl: String = ""
) {
    companion object {
        const val GENDER_MALE = 10301
        const val GENDER_FEMALE = 10302
        const val GENDER_OTHER = 10303
        const val GENDER_NA = 10304
    }
}