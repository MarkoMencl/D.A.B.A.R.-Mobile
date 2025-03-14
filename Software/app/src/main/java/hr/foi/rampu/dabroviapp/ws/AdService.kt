package hr.foi.rampu.dabroviapp.ws

import Ad
import AdWithImageRequest
import Image
import DeleteAdRequest
import UpdateAdRequest
import LinkFavouriteAdRequest
import DeleteFavoriteAdRequest
import UpdateViewCountRequest
import retrofit2.http.PATCH
import LinkImageToAdRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.HTTP

data class LastInsertedAdIdResponse(val lastInsertedAdId: Int)

interface AdService {
    @GET("ad.php")
    suspend fun getAllAds(): Response<List<Ad>>

    @GET("category.php")
    suspend fun getAdsForTheCategory(@Query("categoryId") categoryId: Int): Response<List<Ad>>

    @POST("ad.php")
    suspend fun addAd(@Body adWithImage: AdWithImageRequest): Response<Void>

    @PATCH("ad.php")
    suspend fun updateAd(@Body request: UpdateAdRequest): Response<Void>

    @HTTP(method = "DELETE", path = "ad.php", hasBody = true)
    suspend fun deleteAd(@Body request: DeleteAdRequest): Response<Void>

    @GET("ad.php")
    suspend fun searchAds(
        @Query("search") keyword: String,
        @Query("sort") sort: String? = null
    ): Response<List<Ad>>

    @GET("ad.php")
    suspend fun getUserAds(@Query("userId") userId: Int): Response<List<Ad>>


    @GET("ad.php")
    suspend fun getUserAdIds(@Query("userId") userId: Int): Response<List<Int>>

    @GET("ad-fetch.php")
    suspend fun getAdById(@Query("adId") adId: Int): Response<Ad>

    @GET("ad-fetch.php")
    suspend fun getLastInsertedAdId(@Query("action") action: String = "lastInsertedAdId"): Response<LastInsertedAdIdResponse>

    @POST("categoryCollection.php/linkAdToCategory")
    suspend fun linkAdToCategory(@Query("adId") adId: Int, @Query("categoryId") categoryId: Int): Response<Void>

    @PUT("ad.php")
    suspend fun linkImageToAd(@Body request: LinkImageToAdRequest): Response<Void>


    @POST("favouriteAdCollection.php")
    suspend fun linkFavouriteAd(@Body request: LinkFavouriteAdRequest): Response<Void>

    @GET("ad.php")
    suspend fun getUserFavoriteAds(@Query("favouriteUserId") favouriteUserId: Int): Response<List<Ad>>

    @HTTP(method = "DELETE", path = "deleteFavouriteAd.php", hasBody = true)
    suspend fun deleteFavoriteAd(@Body request: DeleteFavoriteAdRequest): Response<Void>

    @POST("updateViewCount.php")
    suspend fun incrementAdViews(@Body request: UpdateViewCountRequest): Response<Void>


}
