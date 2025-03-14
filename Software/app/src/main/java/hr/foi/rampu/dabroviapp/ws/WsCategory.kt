package hr.foi.rampu.dabroviapp.ws

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import Ad
import Location
import retrofit2.Response
import android.util.Log
import hr.foi.rampu.dabroviapp.entities.Category

object WsCategory {

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

    private val CategoryService = retrofit.create(CategoryService::class.java)

    suspend fun getAllCategories(): List<Category>? {
        val response = CategoryService.getAllCategories()
        return if (response.isSuccessful) response.body() else null
    }
}
