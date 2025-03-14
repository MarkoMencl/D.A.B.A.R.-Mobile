package hr.foi.rampu.dabroviapp.ws

data class ChatRequest (
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    val date: String
)