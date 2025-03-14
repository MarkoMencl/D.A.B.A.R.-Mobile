package hr.foi.rampu.dabroviapp.entities

import com.google.gson.annotations.SerializedName

data class Review(
    val id: Int,
    val value: String,
    val comment: String,
    @SerializedName("username") val reviewerUsername: String,
    @SerializedName("user_id_ratee") val userId: Int,
    @SerializedName("profile_img") val profileImage: String
)
