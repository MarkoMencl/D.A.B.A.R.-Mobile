package hr.foi.rampu.dabroviapp.ws

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import Ad
import DeleteAdRequest
import UpdateAdRequest
import LinkFavouriteAdRequest
import AdWithImageRequest
import Image
import LinkImageToAdRequest
import PostAd
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import retrofit2.Response
import DeleteFavoriteAdRequest
import UpdateViewCountRequest

object WsAd {

    private const val BASE_URL = "http://157.230.8.219/dabar/"
    private const val PHP_USERNAME = "dabrovi2003"
    private const val PHP_PASSWORD = "HF;p3P"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", Credentials.basic(PHP_USERNAME, PHP_PASSWORD))
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    internal val adService = retrofit.create(AdService::class.java)

    suspend fun getAllAds(): List<Ad>? {
        val response = adService.getAllAds()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getAdsForTheCategory(categoryId: Int): List<Ad>? {
        val response = adService.getAdsForTheCategory(categoryId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun addAd(ad: PostAd, image: Image): Boolean {
        val imageWithBase64 = Image(bitmap = image.bitmap, format = image.format, size = image.size)
        val adWithImageRequest = AdWithImageRequest(ad = ad, image = imageWithBase64)
        val response = adService.addAd(adWithImageRequest)

        if (!response.isSuccessful) {
            Log.e("WsAd", "Failed to add ad: ${response.errorBody()?.string()}")
        }

        Log.d("Response", response.toString())
        return response.isSuccessful
    }



    suspend fun deleteAd(adId: Int): Boolean {
        Log.d("WsAd", "Requesting delete for ad ID: $adId")
        val response = adService.deleteAd(DeleteAdRequest(adId))
        return response.isSuccessful
    }



    suspend fun updateAd(adId: Int, title: String? = null, description: String? = null, price: Double? = null): Boolean {
        val request = UpdateAdRequest(adId, title, description, price)
        Log.d("WsAd", "Sending update request: $request")

        val response = adService.updateAd(request)

        return if (response.isSuccessful) {
            Log.d("WsAd", "Ad updated successfully: $adId")
            true
        } else {
            Log.e("WsAd", "Failed to update ad: ${response.errorBody()?.string()}")
            false
        }
    }

    suspend fun searchAds(keyword: String, sort: String? = null): List<Ad>? {
        val response = adService.searchAds(keyword, sort)
        return if (response.isSuccessful) response.body() else null
    }


    suspend fun getUserAdIds(userId: Int): List<Int>? {
        val response = adService.getUserAdIds(userId)
        return if (response.isSuccessful) response.body() else null
    }


    suspend fun getUserAds(userId: Int): List<Ad>? {
        val response = adService.getUserAds(userId)
        return if (response.isSuccessful) response.body() else null
    }


    suspend fun getAdById(adId: Int): Ad? {
        return try {
            val response = adService.getAdById(adId)
            if (response.isSuccessful) {
                val ad = response.body()
                if (ad != null) {
                    Log.d("WsAd", "Ad fetched successfully: $ad")
                    ad
                } else {
                    Log.e("WsAd", "Ad not found")
                    null
                }
            } else {
                Log.e("WsAd", "API Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("WsAd", "Network Error: ${e.message}", e)
            null
        }
    }






    suspend fun getLastInsertedAdId(): Int? {
        val response = adService.getLastInsertedAdId()
        Log.d("RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()?.lastInsertedAdId
        } else {
            Log.e("WsAd", "Failed to fetch last inserted ad ID: ${response.errorBody()?.string()}")
            null
        }
    }

    suspend fun linkImageToAd(adId: Int, imageId: Int): Boolean {
        val request = LinkImageToAdRequest(adId, imageId)
        val response = adService.linkImageToAd(request)
        if (!response.isSuccessful) {
            Log.e("WsAd", "Failed to link image $imageId to ad $adId: ${response.errorBody()?.string()}")
            return false
        }
        return true
    }

    suspend fun linkFavouriteAd(adId: Int, userId: Int): Boolean {
        val request = LinkFavouriteAdRequest(ad_id = adId, user_id = userId)
        val response: Response<Void> = adService.linkFavouriteAd(request)
        return response.isSuccessful
    }

    suspend fun getUserFavoriteAds(userId: Int): List<Ad>? {
        val response = adService.getUserFavoriteAds(userId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun deleteFavoriteAd(adId: Int, userId: Int): Boolean {
        val request = DeleteFavoriteAdRequest(ad_id = adId, user_id = userId)
        val response = adService.deleteFavoriteAd(request)
        return response.isSuccessful
    }

    suspend fun incrementAdViews(adId: Int): Boolean {
        val request = UpdateViewCountRequest(adId)
        val response = adService.incrementAdViews(request)
        return response.isSuccessful
    }
}
