data class UpdateAdRequest(
    val adId: Int,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null
)
