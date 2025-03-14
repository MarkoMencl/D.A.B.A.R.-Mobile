package hr.foi.rampu.dabroviapp.entities

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Chat(
    @SerializedName("user_id_sender") val senderId: Int,
    @SerializedName("user_id_receiver") val receiverId: Int,
    @SerializedName("sender_username") val senderUsername: String,
    @SerializedName("receiver_username") val receiverUsername: String,
    val content: String,
    val date: Date
)
