package hr.foi.rampu.dabroviapp.ws

data class UpdateUserRequest(
    val username: String,
    val email: String,
    val contact: String,
    val profileImg: String?,
    val language: String,
    val locationId: Int?
)


