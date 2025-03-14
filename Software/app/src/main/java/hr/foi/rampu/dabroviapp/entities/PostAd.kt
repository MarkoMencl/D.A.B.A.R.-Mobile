data class PostAd(
    val title: String,
    var description: String,
    var price: Double,
    val userId: Int,
    val status: Int,
    val dateOfPublication: String = "",
    val sellerConfirm: Boolean = false,
    val buyerConfirm: Boolean = false,
    val views: Int = 0,
    val category_id: Int,
    val location_id: Int
)
