package hr.foi.rampu.dabroviapp.ws

import Image
import android.util.Log
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WsImg {

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

    private val imgService = retrofit.create(ImgService::class.java)

    suspend fun getLastInsertedImgId(): Int? {
        val response = imgService.getLastInsertedImgId()

        Log.d("RESPONSEIMG", "Response: ${response.body().toString()}")
        return if (response.isSuccessful) {
            response.body()?.last_inserted_id
        } else {
            Log.e("WsImg", "Failed to fetch last inserted image ID: ${response.errorBody()?.string()}")
            null
        }
    }

    suspend fun getTheImageByAdId(adId: Int): Image? {
        val response = imgService.getTheImageByAdId(adId)

        Log.d("RESPONSEIMG", "Response: ${response.toString()}")
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e("WsImg", "Failed to fetch last inserted image ID: ${response.errorBody()?.string()}")
            null
        }
    }

    suspend fun getImagesByAdId(adId: Int): List<Image>? {
        val response = imgService.getImagesByAdId(adId)

        Log.d("RESPONSEIMG", "Response: ${response.toString()}")
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e("WsImg", "Failed to fetch last inserted image ID: ${response.errorBody()?.string()}")
            null
        }
    }
}