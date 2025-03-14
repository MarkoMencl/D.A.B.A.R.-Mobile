package hr.foi.rampu.dabroviapp.entities

import Ad
import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val contact: String?,
    @SerializedName("profile_img") var profileImg: String?,
    val language: String?,
    @SerializedName("location_id") val locationId: Int?,
)
