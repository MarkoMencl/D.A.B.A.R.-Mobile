package hr.foi.rampu.dabroviapp.ws

data class ReviewRequest(
    val reviewerId: Int,
    val revieweeId: Int,
    val value: String,
    val comment: String,
)
