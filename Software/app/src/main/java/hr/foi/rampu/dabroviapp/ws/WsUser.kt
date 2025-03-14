package hr.foi.rampu.dabroviapp.ws

import android.util.Log
import hr.foi.rampu.dabroviapp.entities.User
import hr.foi.rampu.dabroviapp.ws.WsAd.adService
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WsUser {
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

    val userService = retrofit.create(UserService::class.java)

    suspend fun getUserById(userId: Int): User? {
        return try {
            val response = userService.getUserById(userId)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Log.d("WsUser", "User fetched successfully: $user")
                    user
                } else {
                    Log.e("WsUser", "Ad not found")
                    null
                }
            } else {
                Log.e("WsUser", "API Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("WsUser", "Network Error: ${e.message}", e)
            null
        }
    }
}