package hr.foi.rampu.dabroviapp.ws

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WsReview {
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

    val reviewService = retrofit.create(ReviewService::class.java)
}