package hr.foi.rampu.dabroviapp.entities

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ChatPreview(
    @SerializedName("user_id") val id: Int,
    val username: String,
    val content: String,
    val date: Date,
    @SerializedName("profile_img") val profileImage: String
)
