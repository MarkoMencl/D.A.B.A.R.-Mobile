package hr.foi.rampu.dabroviapp.ws

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import Location

object WsLocation {

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

    private val LocationService = retrofit.create(LocationService::class.java)

    suspend fun getAllLocations(): List<Location>? {
        val response = LocationService.getAllLocations()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getLocationById(id: Int): Location? {
        val response = LocationService.getLocationById(id)
        return if (response.isSuccessful) response.body() else null
    }
}
