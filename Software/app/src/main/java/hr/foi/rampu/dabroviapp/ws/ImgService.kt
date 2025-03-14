package hr.foi.rampu.dabroviapp.ws

import Ad
import AdWithImageRequest
import Image
import LinkImageToAdRequest
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface ImgService {
    data class LastInsertedImageId(val last_inserted_id: Int)

    @GET("img-fetch-last.php")
    suspend fun getLastInsertedImgId(@Query("action") action: String = "lastInsertedImgId"): Response<LastInsertedImageId>


    @GET("img.php")
    suspend fun getTheImageByAdId(@Query("ad_id") adId: Int): Response<Image>  // Use the ad_id parameter to get image by ad ID

    @GET("img-fetch.php")
    suspend fun getImagesByAdId(@Query("ad_id") adId: Int): Response<List<Image>>  // Use the ad_id parameter to get image by ad ID
}
