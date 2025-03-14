import com.google.gson.annotations.SerializedName

data class Ad(
    var id: Int? = null,
    val title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    val userId: Int = 0,
    val dateOfPublication: String = "",
    val status: Int = 0,
    @SerializedName("seller_confirm")
    val sellerConfirm: Int = 0,
    @SerializedName("buyer_confirm")
    val buyerConfirm: Int = 0,
    val views: Int = 0,
    @SerializedName("category_id")
    val categoryId: Int = 0,
    @SerializedName("location_id")
    val locationId: Int = 0
)
